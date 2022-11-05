package team.bakkas.applicationcommand.validator

import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils
import team.bakkas.applicationcommand.grpc.ifs.ShopGrpcClient
import team.bakkas.applicationcommand.grpc.ifs.ShopReviewGrpcClient
import team.bakkas.clientcommand.shopReview.ShopReviewCommand
import team.bakkas.clientcommand.shopReview.annotations.ReviewCreatable
import team.bakkas.common.error.ErrorResponse
import team.bakkas.common.exceptions.RequestFieldException
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.shop.ShopNotFoundException
import team.bakkas.common.exceptions.shopReview.ShopReviewNotFoundException
import team.bakkas.servicecommand.validator.ShopReviewValidator
import team.bakkas.dynamo.shopReview.ShopReview

/** Shop Review에 대한 검증을 수행하는 Validator class
 * @param shopGrpcClient
 * @param shopReviewGrpcClient
 */
@Component
class ShopReviewValidatorImpl(
    private val shopGrpcClient: ShopGrpcClient,
    private val shopReviewGrpcClient: ShopReviewGrpcClient
) : ShopReviewValidator() {

    override fun supports(clazz: Class<*>): Boolean {
        return ShopReviewCommand.CreateRequest::class.java.isAssignableFrom(clazz)
    }

    // 해당 리뷰가 생성 가능한지 검증하는 메소드
    override suspend fun validateCreatable(request: ShopReviewCommand.CreateRequest) = with(request) {
        val errors = BeanPropertyBindingResult(this, ShopReviewCommand.CreateRequest::class.java.name)

        validate(this, errors)

        // WebClient를 이용해서 해당 shop이 존재하는지 여부만 뽑아온다
        val isShopExists: Boolean = shopGrpcClient.isExistShop(shopId).result

        // shop이 존재하지 않는 경우 예외를 발생시킨다
        check(isShopExists) {
            throw ShopNotFoundException("shop review에 대응하는 shop이 존재하지 않습니다.")
        }
    }

    // 해당 review가 삭제 가능한지 검증하는 메소드
    override suspend fun validateDeletable(reviewId: String) {
        // reviewId가 비어서 들어오는 경우 예외 처리
        check(reviewId.isNotEmpty()) {
            throw RequestParamLostException("reviewId is lost!!")
        }

        // 해당 리뷰가 실제 존재하는건지는 체크해본다
        val reviewResultMono = shopReviewGrpcClient.isExistShopReview(reviewId).result

        check(reviewResultMono) {
            throw ShopReviewNotFoundException("shop review가 존재하지 않습니다.")
        }
    }

    // reviewId, reviewTitle, shopId, shopName, reviewContent : 비어있는지 검증
    // reviewContent는 200자 이상으로는 못 쓰도록 검증한다
    override fun validate(target: Any, errors: Errors) {
        target::class.java.annotations.map {
            // annotation에 따라서 분기한다
            when (it) {
                is ReviewCreatable -> rejectEmptyByFieldList(
                    errors,
                    listOf("reviewTitle", "shopId", "reviewContent")
                )
            }
        }

        val review = target as ShopReviewCommand.CreateRequest

        // reviewContent의 길이를 200으로 제한한다
        if (review.reviewContent.length > 200) {
            errors.rejectValue(
                "reviewContent",
                "field.max.length",
                arrayOf(review.reviewContent.length),
                "review의 내용은 200을 넘어서는 안됩니다."
            )
        }

        // reivewScore가 0점인 경우 제한한다
        if (review.reviewScore <= 0 || review.reviewScore > 10) {
            errors.rejectValue(
                "reviewScore",
                "field.value.range",
                arrayOf(review.reviewScore),
                "review score은 무조건 0 초과 10 이하입니다."
            )
        }

        // Field에 error가 발생하여 errors에 content가 채워져서 보내진 경우 RequestFieldException을 일으킨다
        check(errors.allErrors.isEmpty()) {
            val errorList = errors.allErrors.map {
                ErrorResponse.FieldError.of(
                    it.objectName,
                    it.arguments.contentToString(),
                    it.defaultMessage!!
                )
            }
            throw RequestFieldException(errorList, "잘못된 요청입니다")
        }
    }
}