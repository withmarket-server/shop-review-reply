package team.bakkas.applicationcommon.advice

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import team.bakkas.common.exceptions.RegionNotKoreaException
import team.bakkas.common.exceptions.RequestBodyLostException
import team.bakkas.common.exceptions.RequestFieldException
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.shop.ShopBranchInfoInvalidException
import team.bakkas.common.exceptions.shopReview.ShopReviewListInvalidException
import team.bakkas.common.error.ErrorCode
import team.bakkas.common.exceptions.shop.ShopNotFoundException
import team.bakkas.common.exceptions.shopReview.ShopReviewNotFoundException

@Component
class GlobalErrorAttributes : DefaultErrorAttributes() {

    override fun getErrorAttributes(request: ServerRequest?, options: ErrorAttributeOptions?): MutableMap<String, Any> {
        val throwable = getError(request)
        val map = getErrorAttributesMap(super.getErrorAttributes(request, options), throwable)

        return map
    }

    // errorMap에 내용물을 채워서 반환해주는 메소드
    private fun getErrorAttributesMap(map: MutableMap<String, Any>, throwable: Throwable): MutableMap<String, Any> {
        map["code"] = HttpStatus.BAD_REQUEST

        when (throwable) {
            is ShopNotFoundException -> {
                map["default_message"] = throwable.message
                map["error"] = ErrorCode.ENTITY_NOT_FOUND
                map["error_code"] = ErrorCode.ENTITY_NOT_FOUND
            }
            is ShopBranchInfoInvalidException -> {
                map["default_message"] = throwable.message
                map["error"] = ErrorCode.INVALID_INFO
                map["error_code"] = ErrorCode.INVALID_INFO
            }
            is ShopReviewNotFoundException -> {
                map["default_message"] = throwable.message
                map["error"] = ErrorCode.ENTITY_NOT_FOUND
                map["error_code"] = ErrorCode.ENTITY_NOT_FOUND
            }
            is ShopReviewListInvalidException -> {
                map["default_message"] = throwable.message
                map["error"] = ErrorCode.INVALID_SHOP_REVIEW_LIST
                map["error_code"] = ErrorCode.INVALID_SHOP_REVIEW_LIST
            }
            is RegionNotKoreaException -> {
                map["default_message"] = throwable.message
                map["error"] = ErrorCode.INVALID_INFO
                map["error_code"] = ErrorCode.INVALID_INFO
            }
            is RequestBodyLostException -> {
                map["default_message"] = throwable.message
                map["error"] = ErrorCode.REQUEST_BODY_LOST
                map["error_code"] = ErrorCode.REQUEST_BODY_LOST
            }
            is RequestParamLostException -> {
                map["default_message"] = throwable.message
                map["error"] = ErrorCode.REQUEST_PARAM_LOST
                map["error_code"] = ErrorCode.REQUEST_PARAM_LOST
            }
            is RequestFieldException -> {
                map["default_message"] = throwable.message
                map["error"] = ErrorCode.REQUEST_BODY_LOST
                map["error_code"] = ErrorCode.REQUEST_BODY_LOST
                map["field_error_list"] = throwable.errors
            }
        }

        return map
    }
}