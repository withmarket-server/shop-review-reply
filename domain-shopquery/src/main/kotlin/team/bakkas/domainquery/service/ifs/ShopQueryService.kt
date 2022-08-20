package team.bakkas.domainquery.service.ifs

import team.bakkas.domaindynamo.entity.Shop

interface ShopQueryService {

    suspend fun findShopByIdAndName(shopId: String, shopName: String): Shop

    suspend fun getAllShopList(): List<Shop>
}