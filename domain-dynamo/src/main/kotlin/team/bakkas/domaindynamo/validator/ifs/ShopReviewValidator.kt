package team.bakkas.domaindynamo.validator.ifs

import org.springframework.validation.Validator
import team.bakkas.domaindynamo.entity.ShopReview

interface ShopReviewValidator: Validator {

    // 해당 리뷰가 생성 가능한지 검증하는 메소드
    suspend fun validateCreatable(shopReview: ShopReview)
}