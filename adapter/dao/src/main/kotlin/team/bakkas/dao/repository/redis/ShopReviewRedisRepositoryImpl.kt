package team.bakkas.dao.repository.redis

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.scanAsFlow
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import team.bakkas.common.utils.RedisUtils
import team.bakkas.dynamo.shopReview.ShopReview
import team.bakkas.repository.ifs.redis.ShopReviewRedisRepository
import java.time.Duration
import java.util.StringTokenizer

/**
 * ShopReviewRedisRepositoryImpl(private val shopReviewReactiveRedisTemplate: ReactiveRedisTemplate<String, ShopReview>)
 * ShopReviewRedisRepository의 구현체
 * @param shopReviewReactiveRedisTemplate
 */
@Repository
class ShopReviewRedisRepositoryImpl(
    private val shopReviewReactiveRedisTemplate: ReactiveRedisTemplate<String, ShopReview>
) : ShopReviewRedisRepository {

    override fun cacheReview(shopReview: ShopReview): Mono<ShopReview> = with(shopReview) {
        val reviewKey = RedisUtils.generateReviewRedisKey(reviewId)

        shopReviewReactiveRedisTemplate.opsForValue().set(reviewKey, this, Duration.ofDays(RedisUtils.DAYS_TO_LIVE))
            .thenReturn(this)
    }

    override fun findReviewById(reviewId: String): Mono<ShopReview> =
        shopReviewReactiveRedisTemplate.opsForValue().get(RedisUtils.generateReviewRedisKey(reviewId))

    override fun deleteReview(reviewId: String): Mono<Boolean> {
        val reviewKey = RedisUtils.generateReviewRedisKey(reviewId)

        return shopReviewReactiveRedisTemplate.opsForValue().get(reviewKey)
            .single()
            .flatMap { shopReviewReactiveRedisTemplate.opsForValue().delete(reviewKey) }
            .thenReturn(true)
    }

    override fun getShopReviewsByShopId(shopId: String): Flow<ShopReview> {
        return shopReviewReactiveRedisTemplate.scanAsFlow()
            .filter { key ->
                val tokenizer = StringTokenizer(key, ":")
                tokenizer.nextToken().equals("shopReview")
            }
            .map { shopReviewReactiveRedisTemplate.opsForValue().get(it).awaitSingle() }
            .filter { it.shopId == shopId }
    }
}