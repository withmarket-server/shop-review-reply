package team.bakkas.applicationcommand.handler

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import team.bakkas.applicationcommand.extensions.toEntity
import team.bakkas.clientcommand.shop.ShopCommand
import team.bakkas.common.ResultFactory
import team.bakkas.common.exceptions.RequestBodyLostException
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.servicecommand.validator.ShopValidator
import team.bakkas.eventinterface.eventProducer.ShopEventProducer

/** shop에 대한 command 로직을 담당하는 handler 클래스
 * @param shopCommandService shop에 대한 command service logic을 담당하는 클래스
 * @param resultFactory result에 대한 반환을 담당하는 factory class
 */
@Component
class ShopCommandHandler(
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

        // 생성 가능한지 검증한다
        shopValidator.validateCreatable(shopCreateRequest)

        // shopCreateRequest를 기반으로 entity 객체를 하나 생성한다
        val generatedShop = shopCreateRequest.toEntity()

        // Handler에서 Kafka로 이벤트를 전송하여 Kafka에 생성 책임을 위임한다
        shopEventProducer.propagateShopCreated(generatedShop)

        return@coroutineScope ok().contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getSuccessResult())
    }

    /** shop을 하나 삭제하는 메소드
     * @param request
     * @return ServerResponse
     */
    suspend fun deleteShop(request: ServerRequest): ServerResponse = coroutineScope {
        // query parameter들을 받아온다
        val shopId = request.queryParamOrNull("id") ?: throw RequestParamLostException("request param is lost!!")

        // 해당 요청이 유효한지 검증한다
        shopValidator.validateDeletable(shopId)

        // deleteShop 이벤트를 발행한다
        val shopDeletedEvent = ShopCommand.DeletedEvent.of(shopId)
        shopEventProducer.propagateShopDeleted(shopDeletedEvent)

        return@coroutineScope ok().contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getSuccessResult())
    }
}