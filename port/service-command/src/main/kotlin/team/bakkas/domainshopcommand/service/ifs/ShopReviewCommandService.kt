package team.bakkas.domainshopcommand.service.ifs

import team.bakkas.dynamo.shopReview.ShopReview


interface ShopReviewCommandService {

    // shop에 대한 review를 생성하는 메소드
    suspend fun createReview(shopReview: ShopReview): ShopReview

    // shopReview를 삭제하는 메소드
    suspend fun deleteReview(reviewId: String, reviewTitle: String): ShopReview
}