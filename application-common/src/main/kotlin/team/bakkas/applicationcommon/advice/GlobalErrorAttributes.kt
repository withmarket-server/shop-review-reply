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
        val map = getErrorAttribtesMap(super.getErrorAttributes(request, options), throwable)

        return map
    }

    // errorMap에 내용물을 채워서 반환해주는 메소드
    private fun getErrorAttribtesMap(map: MutableMap<String, Any>, throwable: Throwable): MutableMap<String, Any> {
        map["code"] = HttpStatus.BAD_REQUEST
        map["default_message"] = throwable.message!!

        when (throwable) {
            is ShopNotFoundException -> {
                map["error"] = ErrorCode.ENTITY_NOT_FOUND
                map["error_code"] = ErrorCode.ENTITY_NOT_FOUND.errorCode
            }
            is ShopBranchInfoInvalidException -> {
                map["error"] = ErrorCode.INVALID_INFO
                map["error_code"] = ErrorCode.INVALID_INFO.errorCode
            }
            is ShopReviewNotFoundException -> {
                map["error"] = ErrorCode.ENTITY_NOT_FOUND
                map["error_code"] = ErrorCode.ENTITY_NOT_FOUND.errorCode
            }
            is ShopReviewListInvalidException -> {
                map["error"] = ErrorCode.INVALID_SHOP_REVIEW_LIST
                map["error_code"] = ErrorCode.INVALID_SHOP_REVIEW_LIST.errorCode
            }
            is RegionNotKoreaException -> {
                map["error"] = ErrorCode.INVALID_INFO
                map["error_code"] = ErrorCode.INVALID_INFO.errorCode
            }
            is RequestBodyLostException -> {
                map["error"] = ErrorCode.REQUEST_BODY_LOST
                map["error_code"] = ErrorCode.REQUEST_BODY_LOST.errorCode
            }
            is RequestParamLostException -> {
                map["error"] = ErrorCode.REQUEST_PARAM_LOST
                map["error_code"] = ErrorCode.REQUEST_PARAM_LOST.errorCode
            }
            is RequestFieldException -> {
                map["error"] = ErrorCode.REQUEST_BODY_LOST
                map["error_code"] = ErrorCode.REQUEST_BODY_LOST.errorCode
                map["field_error_list"] = throwable.errors
            }
        }

        return map
    }
}