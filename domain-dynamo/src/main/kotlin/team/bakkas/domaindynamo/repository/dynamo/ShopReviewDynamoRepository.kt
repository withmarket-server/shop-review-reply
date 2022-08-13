package team.bakkas.domaindynamo.repository.dynamo

import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono
import team.bakkas.domaindynamo.entity.ShopReview

interface ShopReviewDynamoRepository {

    // 비동기적으로 review를 하나 가져오는 메소드
    fun findReviewByIdAndTitleAsync(reviewId: String, reviewTitle: String): Mono<ShopReview>

    // review들에 대한 Key의 flow를 반환해주는 메소드
    fun getAllReviewKeyFlowByShopIdAndName(shopId: String, shopName: String): Flow<Pair<String, String>>

    // review를 하나 생성하는 메소드
    fun createReviewAsync(shopReview: ShopReview): Mono<Void>

    // review를 삭제하는 메소드
    fun deleteReviewAsync(reviewId: String, reviewTitle: String): Mono<ShopReview>
}