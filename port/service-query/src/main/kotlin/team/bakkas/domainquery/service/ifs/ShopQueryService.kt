package team.bakkas.domainquery.service.ifs

import team.bakkas.dynamo.shop.Shop

interface ShopQueryService {

    suspend fun findShopById(shopId: String): Shop?

    suspend fun getAllShopList(): List<Shop>
}