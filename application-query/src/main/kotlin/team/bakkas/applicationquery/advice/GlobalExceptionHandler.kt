package team.bakkas.applicationquery.advice

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import team.bakkas.common.error.ErrorCode
import team.bakkas.common.error.ErrorResponse
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.ShopNotFoundException

/** Application-query 전반에서 발생하는 모든 exception을 캐치해서 처리하는 클래스
 * @since 22/05/31
 * @author Brian
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    // ShopNotFoundException를 처리하는 exceptionHandler
    @ExceptionHandler(ShopNotFoundException::class)
    fun handleShopNotFoundException(e: ShopNotFoundException): ResponseEntity<ErrorResponse.Response> {

        logger.warn("Caught ShopNotFoundException!!")
        logger.info(ErrorResponse.Response.of(ErrorCode.ENTITY_NOT_FOUND).toString())

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.Response.of(ErrorCode.ENTITY_NOT_FOUND))
    }

    // RequestParamLostException을 잡아서 처리하는 exceptionHandler
    @ExceptionHandler(RequestParamLostException::class)
    fun handleRequestParamLostException(e: RequestParamLostException): ResponseEntity<ErrorResponse.Response> {

        logger.warn("Caught requestParamException")
        logger.info(ErrorResponse.Response.of(ErrorCode.REQUEST_PARAM_LOST).toString())

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.Response.of(ErrorCode.REQUEST_PARAM_LOST))
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(e: MissingServletRequestParameterException): ResponseEntity<ErrorResponse.Response> {

        logger.warn("Caught Request Parameters lost exception!!")

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.Response.of(ErrorCode.REQUEST_PARAM_LOST))
    }
}