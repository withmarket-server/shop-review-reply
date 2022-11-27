package team.bakkas.applicationquery.handler

import kotlinx.coroutines.coroutineScope
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull
import team.bakkas.applicationquery.extensions.toSimpleResponse
import team.bakkas.common.ResultFactory
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.shopReview.ShopReviewNotFoundException
import team.bakkas.domainquery.service.ifs.ShopReviewQueryService

/**
 * ShopReviewQueryHandler
 * shopReview에 대한 query request를 검증하고, 검증 완료 시 결과값을 반환하는 query handler class
 * @param shopReviewService shopReview에 대한 Business logic을 담당하는 클래스
 */
@Component
class ShopReviewQueryHandler(
    private val shopReviewService: ShopReviewQueryService
) {

    suspend fun findReviewById(request: ServerRequest): ServerResponse = coroutineScope {
        val reviewId = request.queryParamOrNull("id") ?: throw RequestParamLostException("reviewId is lost!!")

        check(reviewId.isNotEmpty()) {
            throw RequestParamLostException("query parameter is lost!!")
        }

        val review =
            shopReviewService.findReviewById(reviewId) ?: throw ShopReviewNotFoundException("review is not found!!")

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getSingleResult(review.toSimpleResponse()))
    }

    suspend fun getReviewListByShopId(request: ServerRequest): ServerResponse = coroutineScope {
        val shopId = request.queryParamOrNull("shop-id") ?: throw RequestParamLostException("shopId is lost!!")

        check(shopId.isNotEmpty()) {
            throw RequestParamLostException("query parameter is lost!!")
        }

        val reviewList = shopReviewService.getReviewsByShopId(shopId)

        check(reviewList.isNotEmpty()) {
            throw ShopReviewNotFoundException("Shop review is not found!!")
        }

        val reviewDtoList = reviewList.map { it.toSimpleResponse() }

        return@coroutineScope ok().contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getMultipleResult(reviewDtoList))
    }
}