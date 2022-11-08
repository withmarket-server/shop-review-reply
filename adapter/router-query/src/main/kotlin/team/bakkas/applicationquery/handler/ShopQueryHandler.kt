package team.bakkas.applicationquery.handler

import kotlinx.coroutines.*
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import team.bakkas.applicationquery.extensions.toSimpleResponse
import team.bakkas.applicationquery.grpc.client.GrpcShopSearchClient
import team.bakkas.common.ResultFactory
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.shop.ShopNotFoundException
import team.bakkas.domainquery.service.ifs.ShopQueryService

/** Shop에 대한 Query logic을 처리하는 Handler class
 * @param shopQueryService shop에 대한 Service 로직들을 저장한 컨포넌트
 */
@Component
class ShopQueryHandler(
    private val shopQueryService: ShopQueryService,
    private val grpcShopSearchClient: GrpcShopSearchClient
) {

    // shopId와 shopName을 기반으로 shop에 대한 response를 반환해주는 메소드
    // http://localhost:10100/v2/shop/simple?id=xx&name=xx
    suspend fun findById(request: ServerRequest): ServerResponse = coroutineScope {
        val shopId = request.queryParamOrNull("id") ?: throw RequestParamLostException("shopId is lost")

        check(shopId.isNotEmpty()) {
            throw RequestParamLostException("Empty query parameter")
        }

        // shop이 발견되지 않으면 ShopNotFoundException을 일으킨다
        val shop = shopQueryService.findShopById(shopId) ?: throw ShopNotFoundException("Shop is not found!!")

        if(shop.deletedAt != null) {
            throw ShopNotFoundException("Shop is not found!!")
        }

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getSingleResult(shop.toSimpleResponse()))
    }

    // 모든 shop에 대한 list를 반환해주는 메소드
    // http://localhost:10100/v2/shop/simple/list
    suspend fun getAllShops(request: ServerRequest): ServerResponse = coroutineScope {
        val shopList = shopQueryService.getAllShopList()

        // shop이 redis에서 하나도 발견되지 않은 경우 예외 처리
        check(shopList.isNotEmpty()) {
            throw ShopNotFoundException("Shop is not found!!")
        }

        val shopResponseList = shopList.map { it.toSimpleResponse() }

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getMultipleResult(shopResponseList))
    }

    // TODO 반경 검색, 카테고리별 검색, 세부 카테고리별 검색, 가게 이름 기반 검색 구현

}