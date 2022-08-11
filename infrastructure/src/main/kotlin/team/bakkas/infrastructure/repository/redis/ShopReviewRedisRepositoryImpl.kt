package team.bakkas.infrastructure.repository.redis

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import team.bakkas.common.utils.RedisUtils
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domaindynamo.repository.redis.ShopReviewRedisRepository
import java.time.Duration

@Repository
class ShopReviewRedisRepositoryImpl(
    private val shopReviewReactiveRedisTemplate: ReactiveRedisTemplate<String, ShopReview>
) : ShopReviewRedisRepository {

    override fun cacheReview(shopReview: ShopReview): Mono<Boolean> = with(shopReview) {
        val reviewKey = RedisUtils.generateReviewRedisKey(reviewId, reviewTitle)

        shopReviewReactiveRedisTemplate.opsForValue().set(reviewKey, this, Duration.ofDays(RedisUtils.DAYS_TO_LIVE))
    }
}