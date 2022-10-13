package team.bakkas.infrastructure.repository.redis

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
import team.bakkas.repository.ifs.redis.ShopRedisRepository
import java.time.Duration
import java.util.StringTokenizer

// Shop을 redis에 캐싱하는 메소드들을 정의하는 repository
@Repository
class ShopRedisRepositoryImpl(
    private val shopReactiveRedisTemplate: ReactiveRedisTemplate<String, Shop>
) : ShopRedisRepository {

    // shop을 캐싱하는 메소드
    override fun cacheShop(shop: Shop): Mono<Boolean> = with(shop) {
        val shopKey = RedisUtils.generateShopRedisKey(shopId, shopName)

        shopReactiveRedisTemplate.opsForValue().set(shopKey, this, Duration.ofDays(RedisUtils.DAYS_TO_LIVE))
    }

    // redis에 저장된 shop을 가져오는 메소드
    override fun findShopByKey(shopKey: String): Mono<Shop> = shopReactiveRedisTemplate.opsForValue().get(shopKey)

    // DynamoDB에 저장된 모든 shop을 가져오는 메소드
    override fun getAllShops(): Flow<Shop> {
        return shopReactiveRedisTemplate.scanAsFlow()
            .filter { key -> StringTokenizer(key, ":").nextToken().equals("shop") }
            .map { findShopByKey(it).awaitSingle() }
    }
}