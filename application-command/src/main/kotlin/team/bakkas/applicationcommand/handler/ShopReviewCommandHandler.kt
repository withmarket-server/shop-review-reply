package team.bakkas.applicationcommand.handler

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.MediaType
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import team.bakkas.applicationcommand.kafka.KafkaTopics
import team.bakkas.clientcommand.dto.ShopCommand
import team.bakkas.clientcommand.dto.ShopReviewCommand
import team.bakkas.common.ResultFactory
import team.bakkas.common.exceptions.RequestBodyLostException
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domainshopcommand.service.ifs.ShopReviewCommandService

/** ShopReview에 대한 command handler class
 * @param shopReviewCommandService
 * @param shopReviewKafkaTemplate
 * @param reviewCountEventKafkaTemplate
 * @param resultFactory
 */
@Component
class ShopReviewCommandHandler(
    private val shopReviewCommandService: ShopReviewCommandService,
    private val shopReviewKafkaTemplate: KafkaTemplate<String, ShopReview>,
    private val reviewCountEventKafkaTemplate: KafkaTemplate<String, ShopCommand.ReviewCountEventDto>,
    private val resultFactory: ResultFactory
) {

    // shopReview를 하나 생성하는 메소드
    suspend fun createReview(request: ServerRequest): ServerResponse = coroutineScope {
        // 비동기적으로 reviewDto를 body로부터 뽑아온다
        val reviewCreateDto = request.bodyToMono(ShopReviewCommand.CreateDto::class.java)
            .awaitSingleOrNull()

        // body가 유실되어있는지 검증
        checkNotNull(reviewCreateDto) {
            throw RequestBodyLostException("Body is lost!!")
        }

        // TODO service의 createReview 로직 작성
        val createdReview = shopReviewCommandService.createReview(reviewCreateDto)

        // Kafka에 이벤트를 전파하는 로직
        with(createdReview) {
            // 생성된 review를 redis에서 처리하도록 이벤트 발행
            shopReviewKafkaTemplate.send(KafkaTopics.shopReviewCreateTopic, this)

            // review가 생성되었음을 shop table로 전파
            reviewCountEventKafkaTemplate.send(
                KafkaTopics.reviewCountEventTopic, ShopCommand.ReviewCountEventDto(
                    shopId, shopName, true, reviewScore
                )
            )
        }

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(resultFactory.getSuccessResult())
    }
}