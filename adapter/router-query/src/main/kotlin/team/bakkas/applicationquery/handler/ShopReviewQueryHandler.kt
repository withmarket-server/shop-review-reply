package team.bakkas.applicationquery.handler

import kotlinx.coroutines.coroutineScope
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull
import team.bakkas.clientquery.dto.ShopReviewQuery
import team.bakkas.common.ResultFactory
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.shopReview.ShopReviewNotFoundException
import team.bakkas.domainquery.service.ifs.ShopReviewQueryService
import team.bakkas.dynamo.shopReview.ShopReview

/** ShopReview에 대한 query logic에 대한 handler class
 * @param shopReviewService shopReview에 대한 Business logic을 담당하는 클래스
 */
@Component
class ShopReviewQueryHandler(
    private val shopReviewService: ShopReviewQueryService
) {

    /** reviewId와 reviewTitle을 기반으로 review 하나를 가져오는 메소드
     * @param reviewId review의 id
     * @param reviewTitle review의 제목
     * @throws RequestParamLostException
     * @return ServerResponse
     */
    suspend fun findReviewByIdAndTitle(request: ServerRequest): ServerResponse = coroutineScope {
        // id와 title을 request로부터 받아오고, 존재하지 않으면 바로 에러 처리를 수행한다
        val reviewId = request.queryParamOrNull("id") ?: throw RequestParamLostException("reviewId is lost!!")
        val reviewTitle = request.queryParamOrNull("title") ?: throw RequestParamLostException("reviewTitle is lost!!")

        // review가 존재하지 않으면 exception을 일으킨다
        val review = shopReviewService.findReviewByIdAndTitle(reviewId, reviewTitle)
            ?: throw ShopReviewNotFoundException("review is not found!!")

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getSingleResult(toSimpleResponse(review)))
    }

    /** shopId와 shopName을 기반으로 review의 목록을 가져오는 메소드
     * @param shopId shop id
     * @param shopName shop name
     * @throws RequestParamLostException
     * @return ServerResponse
     */
    suspend fun getReviewListByShopIdAndName(request: ServerRequest): ServerResponse = coroutineScope {
        // query param으로부터 shopId와 shopName을 받아오고, 없으면 예외처리
        val shopId = request.queryParamOrNull("shop-id") ?: throw RequestParamLostException("shopId is lost!!")
        val shopName = request.queryParamOrNull("shop-name") ?: throw RequestParamLostException("shopName is lost!!")

        val reviewList = shopReviewService.getReviewListByShop(shopId, shopName)

        // flow에 item이 하나도 전달이 안 되는 경우의 예외 처리
        check(reviewList.isNotEmpty()) {
            throw ShopReviewNotFoundException("Shop review is not found!!")
        }

        val reviewDtoList = reviewList.map { toSimpleResponse(it) }

        return@coroutineScope ok().contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getMultipleResult(reviewDtoList))
    }

    private fun toSimpleResponse(shopReview: ShopReview) = ShopReviewQuery.SimpleResponse(
        reviewId = shopReview.reviewId,
        reviewTitle = shopReview.reviewTitle,
        shopId = shopReview.shopId,
        shopName = shopReview.shopName,
        reviewContent = shopReview.reviewContent,
        reviewScore = shopReview.reviewScore,
        reviewPhotoList = shopReview.reviewPhotoList,
        createdAt = shopReview.createdAt,
        updatedAt = shopReview.updatedAt
    )
}