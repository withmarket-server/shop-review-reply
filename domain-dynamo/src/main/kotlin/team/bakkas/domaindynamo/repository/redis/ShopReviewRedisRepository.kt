package team.bakkas.domaindynamo.repository.redis

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import team.bakkas.domaindynamo.entity.ShopReview

@Repository
class ShopReviewRedisRepository(
    private val shopReviewReactiveRedisTemplate: ReactiveRedisTemplate<String, ShopReview>
) {
    companion object {
        // Cache를 보관할 기간을 정의
        val DAYS_TO_LIVE = 1L

        fun generateRedisKey(reviewId: String, reviewTitle: String) = "shopReview-$reviewId-$reviewTitle"
    }

    fun cacheReview(shopReview: ShopReview) = with(shopReview) {
        val reviewKey = generateRedisKey(reviewId, reviewTitle)

        shopReviewReactiveRedisTemplate.opsForValue().set(reviewKey, this)
    }
}