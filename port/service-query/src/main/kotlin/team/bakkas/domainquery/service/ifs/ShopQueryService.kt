package team.bakkas.domainquery.service.ifs

import team.bakkas.dynamo.shop.Shop

/**
 * ShopQueryService
 * Shop에 대한 Query business logic을 처리하는 service interface
 * Clean Architecture에서 UseCase layer에 대응한다.
 */
interface ShopQueryService {

    suspend fun findShopById(shopId: String): Shop?

    suspend fun getAllShopList(): List<Shop>
}