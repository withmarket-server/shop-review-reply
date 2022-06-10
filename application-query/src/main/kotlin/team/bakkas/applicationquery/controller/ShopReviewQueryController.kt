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
import team.bakkas.clientmobilequery.dto.ShopReviewBasicReadDto
import team.bakkas.common.Results
import team.bakkas.domainqueryservice.service.ShopReviewQueryService

/** ShopReview의 Read를 담당하는 controller class
 * @param shopReviewQueryService service logic of shop_review
 * @author Brian
 * @since 22/06/04
 */
@Api(tags = ["Shop의 리뷰에 대한 읽기 전용 기능을 제공하는 Controller"])
@RestController
@RequestMapping("/v1/shop-review")
class ShopReviewQueryController(
    private val shopReviewQueryService: ShopReviewQueryService
) {
    // logger 생성
    private val logger = LoggerFactory.getLogger(javaClass)

    // http://localhost:10100/v1/shop-review/basic?id=???&title=???
    @GetMapping("/basic")
    @ApiOperation(value = "단일 리뷰만 찾아오는 메소드", notes = "리뷰의 id와 제목을 전달해야한다")
    @ApiImplicitParams(
        value = [
            ApiImplicitParam(
                name = "id", value = "review의 id", required = true,
                dataType = "string", paramType = "query", defaultValue = "None"
            ),
            ApiImplicitParam(
                name = "title", value = "review의 제목", required = true,
                dataType = "string", paramType = "query", defaultValue = "None"
            )
        ]
    )
    fun findShopReviewByIdAndTitle(
        @RequestParam(name = "id") reviewId: String,
        @RequestParam(name = "title") reviewTitle: String
    ): ResponseEntity<Results.SingleResult<ShopReviewBasicReadDto>> {

        logger.info("Call findShopReviewByIdAndTitle()")
        logger.info("Params: reviewId=$reviewId, reviewTitle=$reviewTitle")

        return shopReviewQueryService.findReviewByIdAndName(reviewId, reviewTitle)
    }

    // http://localhost:10100/v1/shop-review/basic-list?shop-id=???&shop-name=???
    @GetMapping("/basic-list")
    @ApiOperation(value = "특정 가게에 대한 모든 리뷰를 가져오는 메소드", notes = "가게의 id와 이름을 전달해야한다")
    @ApiImplicitParams(
        value = [
            ApiImplicitParam(
                name = "shop-id", value = "가게의 id", required = true,
                dataType = "string", paramType = "query", defaultValue = "None"
            ),
            ApiImplicitParam(
                name = "shop-name", value = "가게의 이름", required = true,
                dataType = "string", paramType = "query", defaultValue = "None"
            )
        ]
    )
    fun getShopReviewListByShopKey(
        @RequestParam(name = "shop-id") shopId: String,
        @RequestParam(name = "shop-name") shopName: String
    ): ResponseEntity<Results.MultipleResult<ShopReviewBasicReadDto>> {

        logger.info("Call getShopReviewListByShopKey()")
        logger.info("Params: shopId=$shopId, shopName=$shopName")

        return shopReviewQueryService.getShopReviewListByShopKey(shopId, shopName)
    }
}