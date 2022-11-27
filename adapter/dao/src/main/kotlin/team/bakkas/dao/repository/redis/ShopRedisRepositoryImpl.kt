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

/**
 * ShopRedisRepositoryImpl(private val shopReactiveRedisTemplate: ReactiveRedisTemplate<String, Shop>)
 * ShopRedisRepository의 구현체
 * @param shopReactiveRedisTemplate
 */
@Repository
class ShopRedisRepositoryImpl(
    private val shopReactiveRedisTemplate: ReactiveRedisTemplate<String, Shop>
) : ShopRedisRepository {

    override fun cacheShop(shop: Shop): Mono<Shop> = with(shop) {
        val shopKey = RedisUtils.generateShopRedisKey(shopId)

        shopReactiveRedisTemplate.opsForValue().set(shopKey, this, Duration.ofDays(RedisUtils.DAYS_TO_LIVE))
            .thenReturn(shop)
    }

    override fun findShopByKey(shopKey: String): Mono<Shop> =
        shopReactiveRedisTemplate.opsForValue().get(shopKey)

    override fun getAllShops(): Flow<Shop> {
        return shopReactiveRedisTemplate.scanAsFlow()
            .filter { key -> StringTokenizer(key, ":").nextToken().equals("shop") } // shop prefix가 붙은 key들만 가져온다
            .map { findShopByKey(it).awaitSingle() }
    }

    override fun deleteShop(shopId: String): Mono<Boolean> {
        val shopKey = RedisUtils.generateShopRedisKey(shopId)

        return shopReactiveRedisTemplate.opsForValue().delete(shopKey)
    }
}