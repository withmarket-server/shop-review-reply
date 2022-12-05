package team.bakkas.applicationcommand.validator

import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import team.bakkas.applicationcommand.grpc.ifs.ShopGrpcClient
import team.bakkas.applicationcommand.grpc.ifs.ShopReviewGrpcClient
import team.bakkas.clientcommand.reply.ReplyCommand
import team.bakkas.clientcommand.reply.annotations.ReplyCreatable
import team.bakkas.common.exceptions.shop.MemberNotOwnerException
import team.bakkas.common.exceptions.shopReview.ShopReviewNotFoundException
import team.bakkas.servicecommand.validator.ReplyValidator

/**
 * ReplyValidatorImpl
 * ReplyValidator의 구현체
 * @param shopGrpcClient
 * @param shopReviewGrpcClient
 */
@Component
class ReplyValidatorImpl(
    private val shopGrpcClient: ShopGrpcClient,
    private val shopReviewGrpcClient: ShopReviewGrpcClient
) : ReplyValidator() {

    override fun supports(clazz: Class<*>): Boolean {
        return ReplyCommand.CreateRequest::class.java.isAssignableFrom(clazz)
    }

    override suspend fun validateCreatable(request: ReplyCommand.CreateRequest) = with(request) {
        val errors = BeanPropertyBindingResult(this, ReplyCommand.CreateRequest::class.java.name)

        // validate fields
        validate(this, errors)

        // shopId, memberId를 이용해서 해당 member가 shop의 주인인지 검증한다
        val isOwnerOfShop = with(request) {
            shopGrpcClient.isOwnerOfShop(memberId, shopId)
        }.result

        check(isOwnerOfShop) {
            throw MemberNotOwnerException("해당 member에게 답글 작성 권한이 존재하지 않습니다.")
        }

        // 해당 review가 존재하는지 검증한다
        val isExistsReview = shopReviewGrpcClient.isExistShopReview(reviewId).result

        check(isExistsReview) {
            throw ShopReviewNotFoundException("review가 존재하지 않습니다.")
        }

        // TODO 해당 reviewId에 답글이 존재하는지 여부를 반환하는 로직을 구현

    }

    override fun validate(target: Any, errors: Errors) {
        target::class.java.annotations.map {
            // annotation에 따라서 분기한다
            when(it) {
                is ReplyCreatable -> {
                    rejectEmptyFieldList(
                        errors,
                        listOf("memberId", "shopId", "reviewId", "content")
                    )

                    val reply = target as ReplyCommand.CreateRequest

                    // reply는 100자로 제한한다
                    if (reply.content.length > 100) {
                        errors.rejectValue(
                            "content",
                            "field.max.length",
                            arrayOf(reply.content.length),
                            "답글의 내용은 100자를 넘으면 안됩니다."
                        )
                    }
                }
            }
        }

        throwsExceptionIfErrorExists(errors)
    }
}