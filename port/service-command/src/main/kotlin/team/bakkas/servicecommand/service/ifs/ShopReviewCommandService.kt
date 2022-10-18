package team.bakkas.servicecommand.service.ifs

import reactor.core.publisher.Mono
import team.bakkas.dynamo.shopReview.ShopReview


interface ShopReviewCommandService {

    // shop에 대한 review를 생성하는 메소드
    fun createReview(shopReview: ShopReview): Mono<ShopReview>

    // shopReview를 삭제하는 메소드
    fun deleteReview(reviewId: String, reviewTitle: String): Mono<ShopReview>
}