package team.bakkas.applicationquery.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import team.bakkas.domainqueryservice.service.ShopQueryService

@RestController
@RequestMapping("/v1/shop")
class ShopQueryController(
    private val shopQueryService: ShopQueryService
) {

    // shopId와 shopName을 받아서 그에 해당하는 shop을 반환해주는 메소드
    @GetMapping("/simple")
    fun getShopByIdAndName(@RequestParam(name = "id") shopId: String, @RequestParam(name = "name") shopName: String) =
        shopQueryService.getShopByIdAndName(shopId, shopName)

    // 조건 없이 모든 shop을 반환해주는 메소드
    @GetMapping("/simple/list")
    fun getAllShops() = shopQueryService.getAllShops()
}