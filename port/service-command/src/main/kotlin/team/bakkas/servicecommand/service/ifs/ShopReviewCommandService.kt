package team.bakkas.servicecommand.service.ifs

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import team.bakkas.dynamo.shopReview.ShopReview

/**
 * ShopReviewCommandService
 * ShopReview에 대한 command business logic을 담당하며 처리하는 service class
 * Clean Architecture 상의 UseCase layer에 대응한다
 */
interface ShopReviewCommandService {

    fun createReview(shopReview: ShopReview): Mono<ShopReview>

    fun deleteReview(reviewId: String): Mono<ShopReview>

    fun softDeleteReview(reviewId: String): Mono<ShopReview>

    fun deleteAllReviewsOfShop(shopId: String): Flux<ShopReview>

    fun softDeleteAllReviewsOfShop(shopId: String): Flux<ShopReview>
}