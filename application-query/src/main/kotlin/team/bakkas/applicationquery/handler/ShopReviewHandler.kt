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
import team.bakkas.clientmobilequery.dto.ShopReviewSimpleReadDto
import team.bakkas.common.ResultFactory
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.ShopReviewNotFoundException
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domainqueryservice.repository.ShopReviewRepository

@Component
class ShopReviewHandler(
    private val shopReviewRepository: ShopReviewRepository,
    private val resultFactory: ResultFactory
) {

    /** reviewId와 reviewTitle을 기반으로 review 하나를 가져오는 메소드
     * @param reviewId review의 id
     * @param reviewTitle review의 제목
     */
    suspend fun findReviewByIdAndTitle(request: ServerRequest): ServerResponse = coroutineScope {
        // id와 title을 request로부터 받아오고, 존재하지 않으면 바로 에러 처리를 수행한다
        val reviewId = request.queryParamOrNull("id") ?: throw RequestParamLostException("reviewId is lost!!")
        val reviewTitle = request.queryParamOrNull("title") ?: throw RequestParamLostException("reviewTitle is lost!!")

        val reviewMono = shopReviewRepository.findShopReviewByIdAndTitleWithCaching(reviewId, reviewTitle)
        val reviewDeferred = CoroutinesUtils.monoToDeferred(reviewMono)

        return@coroutineScope withContext(Dispatchers.IO) {
            reviewDeferred.await()
        }?.let {
            ok().contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(resultFactory.getSingleResult(toSimpleReadDto(it)))
        } ?: throw ShopReviewNotFoundException("Shop review is not found!!")
    }

    suspend fun getReviewListByShopIdAndName(request: ServerRequest): ServerResponse = coroutineScope {
        // query param으로부터 shopId와 shopName을 받아오고, 없으면 예외처리
        val shopId = request.queryParamOrNull("shop-id") ?: throw RequestParamLostException("shopId is lost!!")
        val shopName = request.queryParamOrNull("shop-name") ?: throw RequestParamLostException("shopName is lost!!")

        val reviewDtoList = mutableListOf<ShopReviewSimpleReadDto>()
        val reviewFlow = shopReviewRepository.getShopReviewListFlowByShopIdAndNameWithCaching(shopId, shopName)

        // 비동기적으로 reviewDtoList에 원소를 담는다
        withContext(Dispatchers.IO) {
            reviewFlow.buffer()
                .collect {
                    val reviewDeferred = CoroutinesUtils.monoToDeferred(it)
                    val review = reviewDeferred.await()!!
                    reviewDtoList.add(toSimpleReadDto(review))
                }
        }

        // review가 하나도 없다면 예외 처리
        check(reviewDtoList.size != 0) {
            throw ShopReviewNotFoundException("shopReview is not found!!")
        }

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