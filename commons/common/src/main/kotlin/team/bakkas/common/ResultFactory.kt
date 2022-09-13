package team.bakkas.common

import team.bakkas.common.error.ErrorCode
import team.bakkas.common.error.ErrorResponse

/**
 * Result를 반환하는 메소드들을 모아둔 Factory class
 * @author Brian
 * @since 22/05/22
 */
object ResultFactory {

    // 성공에 대한 결과를 리턴하는 메소드
    fun getSuccessResult(): Results.CommonResult = Results.CommonResult(true)

    // 단일 데이터를 가지는 성공 결과를 반환하는 메소드
    fun <T> getSingleResult(data: T): Results.SingleResult<T> = Results.SingleResult(
        success = true,
        data = data
    )

    // 리스트 형태의 데이터를 가지는 성공 결과를 반환하는 메소드
    fun <T> getMultipleResult(data: List<T>): Results.MultipleResult<T> = Results.MultipleResult(
        success = true,
        data = data
    )

    // field에서 에러가 터지지 않은 경우 에러코드만 포함시켜서 에러를 반환해주는 메소드
    fun getSimpleErrorResult(errorCode: ErrorCode, defaultMessage: String): ErrorResponse.Response =
        ErrorResponse.Response.of(errorCode, defaultMessage)

    // field에서 에러가 발생하였으나 bindingResult가 존재하지 않는 오류에 대해서 에러를 반환해주는 메소드
    fun getErrorResultWithFieldError(
        errorCode: ErrorCode,
        defaultMessage: String,
        fieldErrorList: List<ErrorResponse.FieldError>
    ) =
        ErrorResponse.Response.of(errorCode, defaultMessage, fieldErrorList)
}