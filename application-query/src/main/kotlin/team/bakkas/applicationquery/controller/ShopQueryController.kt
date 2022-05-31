package team.bakkas.applicationquery.controller

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import team.bakkas.clientmobilequery.dto.ShopSimpleReadDto
import team.bakkas.common.Results
import team.bakkas.domainqueryservice.service.ShopQueryService

@RestController
@RequestMapping("/v1/shop")
class ShopQueryController(
    private val shopQueryService: ShopQueryService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    // shopId와 shopName을 받아서 그에 해당하는 shop을 반환해주는 메소드 (simple shop 형태로 반환)
    @GetMapping("/simple")
    fun getSimpleShopByIdAndName(
        @RequestParam(name = "id") shopId: String, @RequestParam(name = "name") shopName: String
    ): ResponseEntity<Results.SingleResult<ShopSimpleReadDto>> {

        logger.info("Call getSimpleShopByIdAndName()")

        return shopQueryService.getShopByIdAndName(shopId, shopName)
    }

    // 조건 없이 모든 shop을 반환해주는 메소드
    @GetMapping("/simple/list")
    fun getAllSimpleShops(): ResponseEntity<Results.MultipleResult<ShopSimpleReadDto>> {

        logger.info("Call getAllSimpleShops()")

        return shopQueryService.getAllSimpleShops()
    }
}