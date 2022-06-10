package team.bakkas.applicationquery.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import team.bakkas.clientmobilequery.dto.ShopSimpleReadDto
import team.bakkas.common.Results
import team.bakkas.domainqueryservice.service.ShopQueryService

/** Shop의 Read 만을 담당하는 controller class
 * @param shopQueryService service logic of shop
 * @since 22/05/29
 * @author Brian
 */
@Api(tags = ["Shop에 대한 읽기 전용 기능을 제공하는 Controller"])
@RestController
@RequestMapping("/v1/shop")
class ShopQueryController(
    private val shopQueryService: ShopQueryService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    // shopId와 shopName을 받아서 그에 해당하는 shop을 반환해주는 메소드 (simple shop 형태로 반환)
    @GetMapping("/simple")
    @ApiOperation(value = "메인 화면에 노출되는 shop에 대한 response을 반환하는 메소드", notes = "shop의 id와 name을 전달해야한다")
    @ApiImplicitParams(
        value = [
            ApiImplicitParam(
                name = "id", value = "shop의 id", required = true,
                dataType = "string", paramType = "query", defaultValue = "None"
            ),
            ApiImplicitParam(
                name = "name", value = "shop의 이름", required = true,
                dataType = "string", paramType = "query", defaultValue = "None"
            )
        ]
    )
    fun getSimpleShopByIdAndName(
        @RequestParam(name = "id") shopId: String, @RequestParam(name = "name") shopName: String
    ): ResponseEntity<Results.SingleResult<ShopSimpleReadDto>> {

        logger.info("Call getSimpleShopByIdAndName()")
        logger.info("Params: shopId=$shopId, shopName=$shopName")

        return shopQueryService.getShopByIdAndName(shopId, shopName)
    }

    // 조건 없이 모든 shop을 반환해주는 메소드
    @GetMapping("/simple/list")
    @ApiOperation(value = "메인 화면에 노출되는 shop list에 대한 response을 반환하는 메소드", notes = "페이징 처리는 없음.")
    fun getAllSimpleShops(): ResponseEntity<Results.MultipleResult<ShopSimpleReadDto>> {

        logger.info("Call getAllSimpleShops()")
        logger.info("Params: None")

        return shopQueryService.getAllSimpleShops()
    }
}