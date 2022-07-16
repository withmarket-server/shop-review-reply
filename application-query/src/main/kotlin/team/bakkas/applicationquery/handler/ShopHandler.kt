package team.bakkas.applicationquery.handler

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import org.springframework.core.CoroutinesUtils
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull
import team.bakkas.clientmobilequery.dto.ShopSimpleReadDto
import team.bakkas.common.ResultFactory
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.ShopNotFoundException
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domainqueryservice.repository.ShopRepository

@Component
class ShopHandler(
    private val shopRepository: ShopRepository,
    private val resultFactory: ResultFactory
) {

    suspend fun findByIdAndName(request: ServerRequest): ServerResponse = coroutineScope {
        val shopId = request.queryParamOrNull("shop-id") ?: throw RequestParamLostException("shopId is lost")
        val shopName = request.queryParamOrNull("shop-name") ?: throw RequestParamLostException("shopName is lost")

        val shopMono = shopRepository.findShopByIdAndNameWithCaching(shopId, shopName)
        val shop = withContext(Dispatchers.IO) {
            CoroutinesUtils.monoToDeferred(shopMono).await() ?: throw ShopNotFoundException("Shop is not found")
        }

        return@coroutineScope ok().contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(toSimpleReadDto(shop))
    }

    private fun toSimpleReadDto(shop: Shop) = ShopSimpleReadDto(
        shopId = shop.shopId,
        shopName = shop.shopName,
        isOpen = shop.isOpen,
        lotNumberAddress = shop.lotNumberAddress,
        roadNameAddress = shop.roadNameAddress,
        latitude = shop.latitude,
        longitude = shop.longitude,
        averageScore = shop.averageScore,
        reviewNumber = shop.reviewNumber,
        mainImage = shop.mainImage,
        shopDescription = shop.shopDescription,
        shopCategory = shop.shopCategory,
        shopDetailCategory = shop.shopDetailCategory,
        isBranch = shop.isBranch,
        branchName = shop.branchName
    )
}