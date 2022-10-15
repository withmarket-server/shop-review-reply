package team.bakkas.servicecommand.service.ifs

import team.bakkas.dynamo.shop.Shop

interface ShopCommandService {

    // shop을 하나 생성하는 메소드
    suspend fun createShop(shop: Shop): Shop

    // shop을 캐싱하는 메소드
    suspend fun cacheShop(shop: Shop): Shop
}