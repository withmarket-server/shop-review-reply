package team.bakkas.applicationcommon.advice

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.badRequest
import reactor.core.publisher.Mono
import team.bakkas.common.ResultFactory
import team.bakkas.common.error.ErrorCode
import team.bakkas.common.error.ErrorResponse
import team.bakkas.common.exceptions.RequestFieldException

// application 전역적으로 exception을 처리하는 Component
@Component
@Order(-2) // 기본적인 예외 핸들링 우선순위는 -1이기 때문에 우선순위를 당겨서 적용한다
class GlobalExceptionHandler(
    private val errorAttributes: GlobalErrorAttributes,
    applicationContext: ApplicationContext,
    serverCodecConfigurer: ServerCodecConfigurer
) : AbstractErrorWebExceptionHandler(
    errorAttributes, WebProperties.Resources(), applicationContext
) {

    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)
    }

    // component 생성시 기본적으로 writer, reader를 설정해준다
    init {
        super.setMessageWriters(serverCodecConfigurer.writers)
        super.setMessageReaders(serverCodecConfigurer.readers)
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes?): RouterFunction<ServerResponse> {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse)
    }

    /** errorResponse를 렌더링하는 메소드
     * @param request
     * @return Mono<ServerResponse>
     */
    private fun renderErrorResponse(request: ServerRequest): Mono<ServerResponse> {
        // exception을 request로부터 가져온다
        val throwable = getError(request)

        logger.error(throwable.toString())

        val errorPropertiesMap = errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults())

        val responseBody = when (throwable) {
            is RequestFieldException -> getFieldErrorResponse(errorPropertiesMap)
            else -> getBusinessErrorResponse(errorPropertiesMap)
        }

        return badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .body(responseBody)
    }

    /** Business Exception을 처리하는 메소드
     * @author Brian
     * @param errorMap
     * @return Mono<ErrorResponse.Response>
     */
    private fun getBusinessErrorResponse(errorMap: MutableMap<String, Any>): Mono<ErrorResponse.Response> {
        // errorCode, defaultErrorMessage, fieldErrorList를 map으로부터 가져온다
        val errorCode = errorMap["error_code"] as ErrorCode
        val defaultMessage = errorMap["default_message"] as String

        return Mono.just(ResultFactory.getSimpleErrorResult(errorCode, defaultMessage))
    }

    /** RequestFieldException을 처리하는 메소드
     * @author Brian
     * @param errorMap
     * @return Mono<ErrorResponse.Response>
     */
    @Suppress("UNCHECKED_CAST") // 캐스팅 관련 컴파일 에러 무시
    private fun getFieldErrorResponse(errorMap: MutableMap<String, Any>): Mono<ErrorResponse.Response> {
        // errorCode, defaultErrorMessage, fieldErrorList를 map으로부터 가져온다
        val errorCode = errorMap["error_code"] as ErrorCode
        val defaultMessage = errorMap["default_message"] as String
        val fieldErrorList = errorMap["field_error_list"] as List<ErrorResponse.FieldError>

        return Mono.just(ResultFactory.getErrorResultWithFieldError(errorCode, defaultMessage, fieldErrorList))
    }
}