package team.bakkas.applicationcommand.handler

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull
import team.bakkas.applicationcommand.extensions.toEntity
import team.bakkas.clientcommand.shopReview.ShopReviewCommand
import team.bakkas.common.ResultFactory
import team.bakkas.common.exceptions.RequestBodyLostException
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.servicecommand.service.ifs.ShopReviewCommandService
import team.bakkas.servicecommand.validator.ShopReviewValidator
import team.bakkas.eventinterface.eventProducer.ShopReviewEventProducer

/**
 * ShopReviewCommandHandler
 * ShopReview에 대해서 들어오는 command request들을 검증하고 이벤트를 발행하는 command handler
 * @param shopReviewValidator ShopReview command request들을 검증하는 class
 * @param shopReviewEventProducer 검증된 request들에 대한 event를 발행하는 event producer
 */
@Component
class ShopReviewCommandHandler(
    private val shopReviewValidator: ShopReviewValidator,
    private val shopReviewEventProducer: ShopReviewEventProducer
) {

    suspend fun createReview(request: ServerRequest): ServerResponse = coroutineScope {
        // 비동기적으로 reviewDto를 body로부터 뽑아온다
        val reviewCreateRequest = request.bodyToMono(ShopReviewCommand.CreateRequest::class.java)
            .awaitSingleOrNull()

        checkNotNull(reviewCreateRequest) {
            throw RequestBodyLostException("Body is lost!!")
        }

        shopReviewValidator.validateCreatable(reviewCreateRequest)

        val generatedReview = reviewCreateRequest.toEntity()

        shopReviewEventProducer.propagateCreatedEvent(generatedReview)

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getSuccessResult())
    }

    suspend fun deleteReview(request: ServerRequest): ServerResponse = coroutineScope {
        val reviewId = request.queryParamOrNull("id") ?: throw RequestParamLostException("reviewId is lost")
        val shopId = request.queryParamOrNull("shop-id") ?: throw RequestParamLostException("shopId is lost")

        shopReviewValidator.validateDeletable(reviewId, shopId)

        val deletedEvent = ShopReviewCommand.DeletedEvent.of(reviewId, shopId)

        shopReviewEventProducer.propagateDeletedEvent(deletedEvent)

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getSuccessResult())
    }
}