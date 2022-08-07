package team.bakkas.applicationcommand.handler

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.MediaType
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import team.bakkas.applicationcommand.kafka.KafkaConsumerGroups
import team.bakkas.applicationcommand.kafka.KafkaTopics
import team.bakkas.clientcommand.dto.ShopReviewCommand
import team.bakkas.common.ResultFactory
import team.bakkas.common.exceptions.RequestBodyLostException
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domainshopcommand.service.ifs.ShopReviewCommandService

@Component
class ShopReviewCommandHandler(
    private val shopReviewCommandService: ShopReviewCommandService,
    private val shopReviewKafkaTemplate: KafkaTemplate<String, ShopReview>,
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

        // Kafka로 보낸다
        shopReviewKafkaTemplate.send(KafkaTopics.shopReviewCreateTopic, createdReview)

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(resultFactory.getSuccessResult())
    }
}