package team.bakkas.domainshopcommand.validator

import org.springframework.validation.Validator
import team.bakkas.dynamo.entity.ShopReview

interface ShopReviewValidator: Validator {

    // 해당 리뷰가 생성 가능한지 검증하는 메소드
    suspend fun validateCreatable(shopReview: ShopReview)

    // 해당 리뷰가 삭제 가능한지 검증하는 메소드
    suspend fun validateDeletable(reviewId: String, reviewTitle: String)
}