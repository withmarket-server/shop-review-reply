package team.bakkas.servicecommand.validator

import org.springframework.validation.Validator
import team.bakkas.dynamo.shopReview.ShopReview

abstract class ShopReviewValidator: Validator, CommonValidator() {

    // 해당 리뷰가 생성 가능한지 검증하는 메소드
    abstract suspend fun validateCreatable(shopReview: ShopReview)

    // 해당 리뷰가 삭제 가능한지 검증하는 메소드
    abstract suspend fun validateDeletable(reviewId: String, reviewTitle: String)
}