package team.bakkas.servicecommand.service.ifs

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import team.bakkas.dynamo.shopReview.ShopReview


interface ShopReviewCommandService {

    // shop에 대한 review를 생성하는 메소드
    fun createReview(shopReview: ShopReview): Mono<ShopReview>

    // shopReview를 삭제하는 메소드
    fun deleteReview(reviewId: String, reviewTitle: String): Mono<ShopReview>

    // shopReview를 softDelete하는 메소드
    fun softDeleteReview(reviewId: String, reviewTitle: String): Mono<ShopReview>

    // shop에 있는 모든 리뷰를 삭제시키는 메소드
    fun deleteAllReviewsOfShop(shopId: String, shopName: String): Flux<ShopReview>

    // shop에 있는 모든 review를 soft delete하는 메소드
    fun softDeleteAllReviewsOfShop(shopId: String, shopName: String): Flux<ShopReview>
}