package team.bakkas.applicationquery.handler

import kotlinx.coroutines.*
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import team.bakkas.domainqueryservice.service.ShopQueryServiceImpl
import team.bakkas.clientmobilequery.dto.ShopSimpleReadDto
import team.bakkas.common.ResultFactory
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.domaindynamo.entity.Shop

@Component
class ShopHandler(
    private val shopService: ShopQueryServiceImpl,
    private val resultFactory: ResultFactory
) {

    // shopId와 shopName을 기반으로 shop에 대한 response를 반환해주는 메소드
    // http://localhost:10100/v2/shop/simple?id=xx&name=xx
    suspend fun findByIdAndName(request: ServerRequest): ServerResponse = coroutineScope {
        val shopId = request.queryParamOrNull("id") ?: throw RequestParamLostException("shopId is lost")
        val shopName = request.queryParamOrNull("name") ?: throw RequestParamLostException("shopName is lost")

        val shop = shopService.findShopByIdAndName(shopId, shopName)

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(resultFactory.getSingleResult(toSimpleReadDto(shop)))
    }

    // 모든 shop에 대한 list를 반환해주는 메소드
    // http://localhost:10100/v2/shop/simple/list
    suspend fun getAllShops(request: ServerRequest): ServerResponse = coroutineScope {
        val shopList = shopService.getAllShopList()
        val shopDtoList = shopList.map {
            toSimpleReadDto(it)
        }

        ok().bodyValueAndAwait(resultFactory.getMultipleResult(shopDtoList))
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