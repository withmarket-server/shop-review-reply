package team.bakkas.applicationcommand.handler

import kotlinx.coroutines.coroutineScope
import org.springframework.core.CoroutinesUtils
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import team.bakkas.clientcommand.dto.shop.ShopCreateDto
import team.bakkas.common.ResultFactory
import team.bakkas.domainshopcommand.service.ShopCommandService

/** shop에 대한 command 로직을 담당하는 handler 클래스
 * @param shopCommandService shop에 대한 command service logic을 담당하는 클래스
 * @param resultFactory result에 대한 반환을 담당하는 factory class
 */
@Component
class ShopCommandHandler(
    private val shopCommandService: ShopCommandService,
    private val resultFactory: ResultFactory
) {

    // shop을 생성하는 메소드
    suspend fun createShop(request: ServerRequest): ServerResponse = coroutineScope {
        val shopDtoMono = request.bodyToMono<ShopCreateDto>()
        val shopDtoDeferred = CoroutinesUtils.monoToDeferred(shopDtoMono)

        // shop을 생성
        val createdShop = shopCommandService.createShop(shopDtoDeferred.await())

        // TODO kafka message를 produce하여 redis에다가 캐시를 반영시킨다


        return@coroutineScope ok().contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(resultFactory.getSuccessResult())
    }
}