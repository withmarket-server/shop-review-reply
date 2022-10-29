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
import team.bakkas.dynamo.shopReview.usecases.softDelete
import team.bakkas.repository.ifs.redis.ShopReviewRedisRepository
import java.time.Duration
import java.util.StringTokenizer

@Repository
class ShopReviewRedisRepositoryImpl(
    private val shopReviewReactiveRedisTemplate: ReactiveRedisTemplate<String, ShopReview>
) : ShopReviewRedisRepository {

    override fun cacheReview(shopReview: ShopReview): Mono<ShopReview> = with(shopReview) {
        val reviewKey = RedisUtils.generateReviewRedisKey(reviewId, reviewTitle)

        shopReviewReactiveRedisTemplate.opsForValue().set(reviewKey, this, Duration.ofDays(RedisUtils.DAYS_TO_LIVE))
            .thenReturn(this)
    }

    // review Key를 이용해서 review를 가져오는 메소드
    override fun findReviewByKey(reviewKey: String): Mono<ShopReview> =
        shopReviewReactiveRedisTemplate.opsForValue().get(reviewKey)

    // review를 삭제하는데 사용하는 메소드
    override fun deleteReview(shopReview: ShopReview): Mono<ShopReview> = with(shopReview) {
        val reviewKey = RedisUtils.generateReviewRedisKey(reviewId, reviewTitle)

        shopReviewReactiveRedisTemplate.opsForValue().get(reviewKey)
            .single()
            .flatMap { shopReviewReactiveRedisTemplate.opsForValue().delete(reviewKey) }
            .thenReturn(this)
    }

    override fun softDeleteReview(reviewId: String, reviewTitle: String): Mono<ShopReview> {
        val reviewKey = RedisUtils.generateReviewRedisKey(reviewId, reviewTitle)

        return findReviewByKey(reviewKey) // reviewId, reviewTitle을 기반으로 review를 찾아와서
            .map { it.softDelete() } // review를 soft delete 처리를 하고
            .flatMap { cacheReview(it) } // 다시 저장한다
    }

    /** 해당 shop의 id와 name을 통해서 shopReview 모두를 가져오는 메소드
     * @param shopId 해당 shop의 id
     * @param shopName 해당 shop의 name
     */
    override fun getShopReviewsByShopId(shopId: String): Flow<ShopReview> {
        return shopReviewReactiveRedisTemplate.scanAsFlow()
            .filter { key ->
                val tokenizer = StringTokenizer(key, ":")
                tokenizer.nextToken().equals("shopReview")
            }
            .map { findReviewByKey(it).awaitSingle() }
            .filter { it.shopId == shopId }
    }
}