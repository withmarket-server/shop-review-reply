package team.bakkas.dao.repository.redis

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.scanAsFlow
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import team.bakkas.common.utils.RedisUtils
import team.bakkas.dynamo.shop.Shop
import team.bakkas.dynamo.shop.usecases.softDelete
import team.bakkas.repository.ifs.redis.ShopRedisRepository
import java.time.Duration
import java.util.StringTokenizer

// Shop을 redis에 캐싱하는 메소드들을 정의하는 repository
@Repository
class ShopRedisRepositoryImpl(
    private val shopReactiveRedisTemplate: ReactiveRedisTemplate<String, Shop>
) : ShopRedisRepository {

    // shop을 캐싱하는 메소드
    override fun cacheShop(shop: Shop): Mono<Shop> = with(shop) {
        val shopKey = RedisUtils.generateShopRedisKey(shopId)

        shopReactiveRedisTemplate.opsForValue().set(shopKey, this, Duration.ofDays(RedisUtils.DAYS_TO_LIVE))
            .thenReturn(shop)
    }

    // redis에 저장된 shop을 가져오는 메소드
    override fun findShopById(shopId: String): Mono<Shop> =
        shopReactiveRedisTemplate.opsForValue().get(RedisUtils.generateShopRedisKey(shopId))

    // DynamoDB에 저장된 모든 shop을 가져오는 메소드
    override fun getAllShops(): Flow<Shop> {
        return shopReactiveRedisTemplate.scanAsFlow()
            .filter { key -> StringTokenizer(key, ":").nextToken().equals("shop") }
            .map { findShopById(it).awaitSingle() }
    }

    // shop을 삭제하는 메소드
    override fun deleteShop(shopId: String): Mono<Boolean> {
        val shopKey = RedisUtils.generateShopRedisKey(shopId)

        return shopReactiveRedisTemplate.opsForValue().delete(shopKey)
    }

    override fun softDeleteShop(shopId: String): Mono<Shop> {
        val shopKey = RedisUtils.generateShopRedisKey(shopId)

        return findShopById(shopKey) // shopId, shopName을 기반으로 shop을 찾아온 다음에
            .map { it.softDelete() } // 해당 shop을 soft delete를 수행하고
            .flatMap { cacheShop(it) } // 해당 shop을 다시 redis에 저장한다
    }
}