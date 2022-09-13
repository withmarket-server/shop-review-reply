package team.bakkas.applicationcommand.handler

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull
import team.bakkas.applicationcommand.extensions.toEntity
import team.bakkas.clientcommand.dto.ShopCommand
import team.bakkas.clientcommand.dto.ShopReviewCommand
import team.bakkas.common.ResultFactory
import team.bakkas.common.exceptions.RequestBodyLostException
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domainshopcommand.service.ifs.ShopReviewCommandService
import team.bakkas.domainshopcommand.validator.ShopReviewValidator
import team.bakkas.eventinterface.kafka.KafkaTopics

/** ShopReview에 대한 command handler class
 * @param shopReviewCommandService
 * @param shopReviewValidator shopReview에 대한 검증을 담당하는 bean
 * @param shopReviewKafkaTemplate
 * @param reviewCountEventKafkaTemplate
 */
@Component
class ShopReviewCommandHandler(
    private val shopReviewCommandService: ShopReviewCommandService,
    private val shopReviewValidator: ShopReviewValidator,
    private val shopReviewKafkaTemplate: KafkaTemplate<String, ShopReview>,
    private val reviewCountEventKafkaTemplate: KafkaTemplate<String, ShopCommand.ReviewCreatedEvent>
) {

    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)
    }

    // shopReview를 하나 생성하는 메소드
    suspend fun createReview(request: ServerRequest): ServerResponse = coroutineScope {
        // 비동기적으로 reviewDto를 body로부터 뽑아온다
        val reviewCreateRequest = request.bodyToMono(ShopReviewCommand.CreateRequest::class.java)
            .awaitSingleOrNull()

        // body가 유실되어있는지 검증
        checkNotNull(reviewCreateRequest) {
            throw RequestBodyLostException("Body is lost!!")
        }

        // dto -> entity 변환 및 검증
        val generatedReview = reviewCreateRequest.toEntity()
        shopReviewValidator.validateCreatable(generatedReview)

        // service의 createReview 로직 호출
        val createdReview = shopReviewCommandService.createReview(generatedReview)

        // Kafka에 리뷰 생성 이벤트를 전파한다
        propagateCreatedEvent(createdReview)

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getSuccessResult())
    }

    // shopReview를 삭제하는 메소드
    suspend fun deleteReview(request: ServerRequest): ServerResponse = coroutineScope {
        val reviewId = request.queryParamOrNull("id") ?: throw RequestParamLostException("reviewId is lost")
        val reviewTitle = request.queryParamOrNull("title") ?: throw RequestParamLostException("reviewTitle is lost")

        // 해당 review가 삭제 가능한지 검증
        shopReviewValidator.validateDeletable(reviewId, reviewTitle)

        // service의 deleteReview 로직 호출
        val deletedReview = shopReviewCommandService.deleteReview(reviewId, reviewTitle)

        // Kafka에 이벤트 전파
        propagateDeletedEvent(deletedReview)

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getSuccessResult())
    }

    /** review 생성 관련 이벤트 전파를 담당하는 메소드
     * @param createdReview 생성된 shopReview
     */
    private fun propagateCreatedEvent(createdReview: ShopReview): Unit = with(createdReview) {
        // 생성된 review를 redis에서 처리하도록 이벤트 발행
        shopReviewKafkaTemplate.send(KafkaTopics.shopReviewCreateTopic, this)

        // review가 생성되었음을 shop table로 전파
        reviewCountEventKafkaTemplate.send(
            KafkaTopics.reviewGenerateEventTopic, ShopCommand.ReviewCreatedEvent(
                shopId, shopName, true, reviewScore
            )
        )
    }

    /** review 삭제 관련 이벤트 전파를 담당하는 메소드
     * @param deletedReview 삭제괸 리뷰를 파라미터로 전달
     */
    private fun propagateDeletedEvent(deletedReview: ShopReview): Unit = with(deletedReview) {
        // 1. redis에 있는 review cache를 삭제하기 위해 이벤트 발행
        shopReviewKafkaTemplate.send(KafkaTopics.shopReviewDeleteTopic, this)

        // 2. dynamoDB의 shop의 review 정보를 갱신하기 위해 이벤트 발행
        reviewCountEventKafkaTemplate.send(
            KafkaTopics.reviewGenerateEventTopic, ShopCommand.ReviewCreatedEvent(
                shopId, shopName, false, reviewScore
            )
        )
    }
}