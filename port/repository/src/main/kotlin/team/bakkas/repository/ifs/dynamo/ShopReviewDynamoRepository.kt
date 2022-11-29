package team.bakkas.repository.ifs.dynamo

import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono
import team.bakkas.dynamo.shopReview.ShopReview

// Dynamo의 ShopReview 테이블에 접근하는데 사용하는 interface
interface ShopReviewDynamoRepository {

    fun findReviewById(reviewId: String): Mono<ShopReview>

    fun getAllReviewsByShopId(shopId: String): Flow<ShopReview>

    fun createReview(shopReview: ShopReview): Mono<ShopReview>

    fun deleteReview(reviewId: String): Mono<ShopReview>

    // soft delete 정책에 의해 shopReview를 dynamo에 삭제 처리를 시행하는 메소드
    // record system으로부터 삭제가 아닌, deletedAt을 갱신하여 삭제 처리를 수행한다
    fun softDeleteReview(reviewId: String): Mono<ShopReview>
}