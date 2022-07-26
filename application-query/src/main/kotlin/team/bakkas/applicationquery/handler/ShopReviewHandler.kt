package team.bakkas.applicationquery.handler

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import org.springframework.core.CoroutinesUtils
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull
import team.bakkas.domainqueryservice.service.ShopReviewService
import team.bakkas.clientmobilequery.dto.ShopReviewSimpleReadDto
import team.bakkas.common.ResultFactory
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.ShopReviewNotFoundException
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domainqueryservice.repository.ShopReviewRepository

@Component
class ShopReviewHandler(
    private val shopReviewService: ShopReviewService,
    private val resultFactory: ResultFactory
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

        val review = shopReviewService.findReviewByIdAndTitle(reviewId, reviewTitle)

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(resultFactory.getSingleResult(toSimpleReadDto(review)))
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
        val reviewDtoList = reviewList.map { toSimpleReadDto(it) }

        return@coroutineScope ok().contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(resultFactory.getMultipleResult(reviewDtoList))
    }

    private fun toSimpleReadDto(shopReview: ShopReview) = ShopReviewSimpleReadDto(
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