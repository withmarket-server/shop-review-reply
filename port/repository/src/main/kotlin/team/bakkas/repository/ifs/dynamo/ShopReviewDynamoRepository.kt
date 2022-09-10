package team.bakkas.repository.ifs.dynamo

import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono
import team.bakkas.domaindynamo.entity.ShopReview

interface ShopReviewDynamoRepository {

    // 비동기적으로 review를 하나 가져오는 메소드
    fun findReviewByIdAndTitle(reviewId: String, reviewTitle: String): Mono<ShopReview>

    // shop에 대한 모든 review를 가져오는 메소드
    fun getAllReviewFlowByShopIdAndName(shopId: String, shopName: String): Flow<ShopReview>

    // review를 하나 생성하는 메소드
    fun createReviewAsync(shopReview: ShopReview): Mono<Void>

    // review를 삭제하는 메소드
    fun deleteReviewAsync(reviewId: String, reviewTitle: String): Mono<ShopReview>
}