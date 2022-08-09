package team.bakkas.infrastructure.repository.redis

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domaindynamo.repository.redis.ShopReviewRedisRepository
import java.time.Duration

@Repository
class ShopReviewRedisRepositoryImpl(
    private val shopReviewReactiveRedisTemplate: ReactiveRedisTemplate<String, ShopReview>
): ShopReviewRedisRepository {
    companion object {
        // Cache를 보관할 기간을 정의
        val DAYS_TO_LIVE = 1L

        fun generateRedisKey(reviewId: String, reviewTitle: String) = "shopReview-$reviewId-$reviewTitle"
    }

    override fun cacheReview(shopReview: ShopReview): Mono<Boolean> = with(shopReview) {
        val reviewKey = generateRedisKey(reviewId, reviewTitle)

        shopReviewReactiveRedisTemplate.opsForValue().set(reviewKey, this, Duration.ofDays(team.bakkas.infrastructure.repository.redis.ShopReviewRedisRepositoryImpl.DAYS_TO_LIVE))
    }
}