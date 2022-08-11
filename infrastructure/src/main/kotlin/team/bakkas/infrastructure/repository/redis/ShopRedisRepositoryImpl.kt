package team.bakkas.infrastructure.repository.redis

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import team.bakkas.common.utils.RedisUtils
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domaindynamo.repository.redis.ShopRedisRepository
import java.time.Duration

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

}