package team.bakkas.repository.ifs.redis

import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono
import team.bakkas.dynamo.shopReview.ShopReview

interface ShopReviewRedisRepository {

    // review를 캐싱하는 메소드
    fun cacheReview(shopReview: ShopReview): Mono<ShopReview>

    // review를 key를 기반으로 가져오는 메소드
    fun findReviewById(reviewId: String): Mono<ShopReview>

    // review를 삭제하는 메소드
    fun deleteReview(shopReview: ShopReview): Mono<ShopReview>

    // review를 soft delete하는 메소드
    fun softDeleteReview(reviewId: String): Mono<ShopReview>

    // shopId, shopName의 정보를 이용하여 해당 shop의 모든 review를 가져오는 메소드
    fun getShopReviewsByShopId(shopId: String): Flow<ShopReview>
}