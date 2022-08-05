package team.bakkas.applicationcommand.handler

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.MediaType
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import team.bakkas.clientcommand.dto.ShopCommand
import team.bakkas.common.ResultFactory
import team.bakkas.common.exceptions.RequestBodyLostException
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domainshopcommand.service.ShopCommandServiceImpl

/** shop에 대한 command 로직을 담당하는 handler 클래스
 * @param shopCommandService shop에 대한 command service logic을 담당하는 클래스
 * @param resultFactory result에 대한 반환을 담당하는 factory class
 */
@Component
class ShopCommandHandler(
    private val shopCommandService: ShopCommandServiceImpl,
    private val shopKafkaTemplate: KafkaTemplate<String, Shop>,
    private val resultFactory: ResultFactory
) {

    companion object {
        val shopCreateTopic = "withmarket.shop.create"
    }

    /** shop을 하나 생성하는 메소드
     * @param request shop 생성에 관한 request
     * @return ServerResponse
     */
    suspend fun createShop(request: ServerRequest): ServerResponse = coroutineScope {
        val shopCreateDto = request.bodyToMono(ShopCommand.ShopCreateDto::class.java)
            .awaitSingleOrNull()

        // body가 비어서 날아오는 경우에 대한 예외 처리
        checkNotNull(shopCreateDto) {
            throw RequestBodyLostException("Body is lost!!")
        }

        // shop을 생성
        val createdShop = shopCommandService.createShop(shopCreateDto)

        // Kafka에다가 생성된 shop을 메시지로 전송하여 consume하는 쪽에서 redis에 캐싱하도록 구현한다
        shopKafkaTemplate.send(shopCreateTopic, createdShop)

        return@coroutineScope ok().contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(resultFactory.getSuccessResult())
    }
}