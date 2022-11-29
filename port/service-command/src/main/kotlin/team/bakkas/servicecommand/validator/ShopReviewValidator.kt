package team.bakkas.servicecommand.validator

import org.springframework.validation.Validator
import team.bakkas.clientcommand.shopReview.ShopReviewCommand

/**
 * ShopReviewValidator
 * ShopReview에 대한 validator의 interface
 */
abstract class ShopReviewValidator: Validator, CommonValidator() {

    // 해당 리뷰가 생성 가능한지 검증하는 메소드
    abstract suspend fun validateCreatable(request: ShopReviewCommand.CreateRequest)

    // 해당 리뷰가 삭제 가능한지 검증하는 메소드
    abstract suspend fun validateDeletable(reviewId: String, shopId: String)
}