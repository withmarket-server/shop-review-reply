package team.bakkas.applicationquery.handler

import kotlinx.coroutines.*
import org.springframework.http.MediaType
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import team.bakkas.clientquery.dto.ShopQuery
import team.bakkas.common.ResultFactory
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.shop.ShopNotFoundException
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domainquery.service.ifs.ShopQueryService
import team.bakkas.eventinterface.kafka.KafkaTopics

/** Shop에 대한 Query logic을 처리하는 Handler class
 * @param shopService shop에 대한 Service 로직들을 저장한 컨포넌트
 * @param shopCountKafkaTemplate shop에 대한 개수 정합이 안 맞을 때 이벤트를 발행해주는 kafkaTemplate
 */
@Component
class ShopQueryHandler(
    private val shopService: ShopQueryService,
    private val shopCountKafkaTemplate: KafkaTemplate<String, ShopQuery.ShopCountEvent>
) {

    // shopId와 shopName을 기반으로 shop에 대한 response를 반환해주는 메소드
    // http://localhost:10100/v2/shop/simple?id=xx&name=xx
    suspend fun findByIdAndName(request: ServerRequest): ServerResponse = coroutineScope {
        val shopId = request.queryParamOrNull("id") ?: throw RequestParamLostException("shopId is lost")
        val shopName = request.queryParamOrNull("name") ?: throw RequestParamLostException("shopName is lost")

        val shop = shopService.findShopByIdAndName(shopId, shopName)

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getSingleResult(toSimpleResponse(shop)))
    }

    // 모든 shop에 대한 list를 반환해주는 메소드
    // http://localhost:10100/v2/shop/simple/list
    suspend fun getAllShops(request: ServerRequest): ServerResponse = coroutineScope {
        val shopList = shopService.getAllShopList()

        // shop이 redis에서 하나도 발견되지 않은 경우 예외 처리
        check(shopList.isNotEmpty()) {
            // 1. shop이 하나도 발견되지 않았으므로 shop을 redis로 올리는 이벤트를 발행한다
            shopCountKafkaTemplate.send(KafkaTopics.shopCountValidateTopic, ShopQuery.ShopCountEvent(0))
            throw ShopNotFoundException("Shop is not found!!")
        }

        val shopResponseList = shopList.map { toSimpleResponse(it) }

        // 2. dynamo에 있는 shop의 개수와 redis에 있는 shop의 개수가 맞는지 검증하는 이벤트를 발행한다
        shopCountKafkaTemplate.send(KafkaTopics.shopCountValidateTopic, ShopQuery.ShopCountEvent(shopResponseList.count()))

        ok().bodyValueAndAwait(ResultFactory.getMultipleResult(shopResponseList))
    }

    private fun toSimpleResponse(shop: Shop) = ShopQuery.SimpleResponse(
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