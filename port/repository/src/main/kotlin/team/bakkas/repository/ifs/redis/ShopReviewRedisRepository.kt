package team.bakkas.repository.ifs.redis

import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono
import team.bakkas.dynamo.shopReview.ShopReview

/**
 * ShopReviewRedisRepository
 * ShopReview 데이터를 redis에 제어하기 위해 사용하는 interface
 */
interface ShopReviewRedisRepository {

    fun cacheReview(shopReview: ShopReview): Mono<ShopReview>

    fun findReviewById(reviewId: String): Mono<ShopReview>

    fun deleteReview(reviewId: String): Mono<Boolean>

    fun getShopReviewsByShopId(shopId: String): Flow<ShopReview>
}