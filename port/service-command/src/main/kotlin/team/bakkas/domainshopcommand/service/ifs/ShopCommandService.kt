package team.bakkas.domainshopcommand.service.ifs

import team.bakkas.clientcommand.dto.ShopCommand
import team.bakkas.domaindynamo.entity.Shop

interface ShopCommandService {

    // shop을 하나 생성하는 메소드
    suspend fun createShop(shop: Shop): Shop

    // shop을 캐싱하는 메소드
    suspend fun cacheShop(shop: Shop): Shop
}