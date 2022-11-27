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

/**
 * ShopCommandHandler
 * Shop command request들을 validate하고 event를 발행하는 command handler
 * @param shopValidator shop request들을 검증하는 validator class
 * @param shopEventProducer 검증된 request들을 이벤트로 발행하는 event producer
 */
@Component
class ShopCommandHandler(
    private val shopValidator: ShopValidator,
    private val shopEventProducer: ShopEventProducer
) {

    suspend fun createShop(request: ServerRequest): ServerResponse = coroutineScope {
        val shopCreateRequest = request.bodyToMono(ShopCommand.CreateRequest::class.java)
            .awaitSingleOrNull()

        checkNotNull(shopCreateRequest) {
            throw RequestBodyLostException("Body is lost!!")
        }

        shopValidator.validateCreatable(shopCreateRequest)

        val generatedShop = shopCreateRequest.toEntity()

        shopEventProducer.propagateShopCreated(generatedShop)

        return@coroutineScope ok().contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getSuccessResult())
    }

    suspend fun deleteShop(request: ServerRequest): ServerResponse = coroutineScope {
        val shopId = request.queryParamOrNull("id") ?: throw RequestParamLostException("request param is lost!!")

        shopValidator.validateDeletable(shopId)

        val shopDeletedEvent = ShopCommand.DeletedEvent.of(shopId)
        shopEventProducer.propagateShopDeleted(shopDeletedEvent)

        return@coroutineScope ok().contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getSuccessResult())
    }

    suspend fun updateShop(request: ServerRequest): ServerResponse = coroutineScope {
        val updateRequest = request.bodyToMono(ShopCommand.UpdateRequest::class.java)
            .awaitSingleOrNull()

        checkNotNull(updateRequest) {
            throw RequestBodyLostException("Body is lost!!")
        }

        shopValidator.validateUpdatable(updateRequest)

        shopEventProducer.propagateShopUpdated(updateRequest)

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getSuccessResult())
    }
}