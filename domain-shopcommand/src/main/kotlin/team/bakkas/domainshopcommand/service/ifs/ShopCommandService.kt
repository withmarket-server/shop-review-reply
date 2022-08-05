package team.bakkas.domainshopcommand.service.ifs

import team.bakkas.clientcommand.dto.shop.ShopCreateDto
import team.bakkas.domaindynamo.entity.Shop

interface ShopCommandService {

    // shop을 하나 생성하는 메소드
    suspend fun createShop(shopCreateDto: ShopCreateDto): Shop


}