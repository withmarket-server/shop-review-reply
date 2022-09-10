package team.bakkas.infrastructure.repository.redis

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.scanAsFlow
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import team.bakkas.common.utils.RedisUtils
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domaindynamo.repository.redis.ShopReviewRedisRepository
import java.time.Duration
import java.util.StringTokenizer

@Repository
class ShopReviewRedisRepositoryImpl(
    private val shopReviewReactiveRedisTemplate: ReactiveRedisTemplate<String, ShopReview>
) : ShopReviewRedisRepository {

    override fun cacheReview(shopReview: ShopReview): Mono<Boolean> = with(shopReview) {
        val reviewKey = RedisUtils.generateReviewRedisKey(reviewId, reviewTitle)

        shopReviewReactiveRedisTemplate.opsForValue().set(reviewKey, this, Duration.ofDays(RedisUtils.DAYS_TO_LIVE))
    }

    // review Key를 이용해서 review를 가져오는 메소드
    override fun findReviewByKey(reviewKey: String): Mono<ShopReview> =
        shopReviewReactiveRedisTemplate.opsForValue().get(reviewKey)

    // review를 삭제하는데 사용하는 메소드
    override fun deleteReview(shopReview: ShopReview): Mono<Boolean> = with(shopReview) {
        val reviewKey = RedisUtils.generateReviewRedisKey(reviewId, reviewTitle)

        shopReviewReactiveRedisTemplate.opsForValue().get(reviewKey)
            .single()
            .flatMap { shopReviewReactiveRedisTemplate.opsForValue().delete(reviewKey) }
    }

    /** 해당 shop의 id와 name을 통해서 shopReview 모두를 가져오는 메소드
     * @param shopId 해당 shop의 id
     * @param shopName 해당 shop의 name
     */
    override fun getShopReviewFlowByShopIdAndName(shopId: String, shopName: String): Flow<ShopReview> {
        return shopReviewReactiveRedisTemplate.scanAsFlow()
            .filter { key ->
                val tokenizer = StringTokenizer(key, ":")
                tokenizer.nextToken().equals("shopReview")
            }
            .map { findReviewByKey(it).awaitSingle() }
            .filter { it.shopId == shopId && it.shopName == shopName }
    }
}