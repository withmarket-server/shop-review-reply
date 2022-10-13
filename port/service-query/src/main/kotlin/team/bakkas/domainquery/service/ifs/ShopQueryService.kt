package team.bakkas.domainquery.service.ifs

import team.bakkas.dynamo.shop.Shop

interface ShopQueryService {

    suspend fun findShopByIdAndName(shopId: String, shopName: String): Shop?

    suspend fun getAllShopList(): List<Shop>
}