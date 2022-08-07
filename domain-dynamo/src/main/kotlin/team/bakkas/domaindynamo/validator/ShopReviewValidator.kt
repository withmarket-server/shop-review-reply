package team.bakkas.domaindynamo.validator

import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils
import org.springframework.validation.Validator
import team.bakkas.common.exceptions.RequestFieldException
import team.bakkas.domaindynamo.entity.ShopReview

@Component
class ShopReviewValidator: Validator {

    fun validateCreatable(shopReview: ShopReview) = with(shopReview) {
        validateFirst(this) // 우선 필드를 모두 검증한다

        // TODO WebClient를 이용해서 review에 대응하는 shop이 이미 존재하는지를 검증한다
    }

    override fun supports(clazz: Class<*>): Boolean {
        return ShopReview::class.java.isAssignableFrom(clazz)
    }

    // reviewId, reviewTitle, shopId, shopName, reviewContent : 비어있는지 검증
    // reviewContent는 200자 이상으로는 못 쓰도록 검증한다
    override fun validate(target: Any, errors: Errors) {
        // reviewId, reviewTitle, shopId, shopName, reviewContent : 비어있는지 검증
        listOf("reviewId, reviewTitle, shopId, shopName, reviewContent").forEach { fieldName ->
            ValidationUtils.rejectIfEmpty(errors, fieldName, "field.required", "${fieldName}이 제공되지 않았습니다.")
        }

        val review = target as ShopReview

        // reviewContent의 길이를 200으로 제한한다
        check(review.reviewContent.length <= 200) {
            errors.rejectValue("reviewContent", "field.max.length", "review의 내용은 200을 넘어서는 안됩니다.")
        }
    }

    // 기본적으로 검증해야하는 메소드
    private fun validateFirst(shopReview: ShopReview) = with(shopReview) {
        val errors = BeanPropertyBindingResult(this, ShopReview::class.java.name)
        validate(this, errors)

        // 기본 조건들을 만족하지 못하면 exception을 터뜨린다
        check(errors == null || errors.allErrors.isEmpty()) {
            throw RequestFieldException(errors.allErrors.toString())
        }
    }
}