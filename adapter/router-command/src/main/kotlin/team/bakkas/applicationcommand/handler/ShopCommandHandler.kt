package team.bakkas.applicationcommand.handler

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import team.bakkas.applicationcommand.extensions.toEntity
import team.bakkas.clientcommand.dto.ShopCommand
import team.bakkas.common.ResultFactory
import team.bakkas.common.exceptions.RequestBodyLostException
import team.bakkas.domainshopcommand.service.ifs.ShopCommandService
import team.bakkas.domainshopcommand.validator.ShopValidator
import team.bakkas.eventinterface.eventProducer.ShopEventProducer

/** shop에 대한 command 로직을 담당하는 handler 클래스
 * @param shopCommandService shop에 대한 command service logic을 담당하는 클래스
 * @param resultFactory result에 대한 반환을 담당하는 factory class
 */
@Component
class ShopCommandHandler(
    private val shopCommandService: ShopCommandService,
    private val shopValidator: ShopValidator,
    private val shopEventProducer: ShopEventProducer
) {

    /** shop을 하나 생성하는 메소드
     * @param request shop 생성에 관한 request
     * @return ServerResponse
     */
    suspend fun createShop(request: ServerRequest): ServerResponse = coroutineScope {
        val shopCreateRequest = request.bodyToMono(ShopCommand.CreateRequest::class.java)
            .awaitSingleOrNull()

        // body가 비어서 날아오는 경우에 대한 예외 처리
        checkNotNull(shopCreateRequest) {
            throw RequestBodyLostException("Body is lost!!")
        }

        // shopCreateRequest를 기반으로 entity 객체를 하나 생성한다
        val generatedShop = shopCreateRequest.toEntity()

        // 생성 가능한지 검증한다
        shopValidator.validateCreatable(generatedShop)

        // shop을 생성
        val createdShop = shopCommandService.createShop(generatedShop)

        // Kafka에다가 생성된 shop을 메시지로 전송하여 consume하는 쪽에서 redis에 캐싱하도록 구현한다
        shopEventProducer.propagateShopCreated(createdShop)

        return@coroutineScope ok().contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getSuccessResult())
    }
}