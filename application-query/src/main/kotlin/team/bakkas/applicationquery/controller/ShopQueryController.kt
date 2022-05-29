package team.bakkas.applicationquery.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.bakkas.domainqueryservice.service.ShopQueryService

@RestController
@RequestMapping("/v1/shop")
class ShopQueryController(
    private val shopQueryService: ShopQueryService
) {

}