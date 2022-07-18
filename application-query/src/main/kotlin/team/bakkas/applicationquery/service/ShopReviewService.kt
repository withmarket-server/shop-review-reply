package team.bakkas.applicationquery.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.springframework.core.CoroutinesUtils
import org.springframework.stereotype.Service
import team.bakkas.common.exceptions.ShopReviewNotFoundException
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domainqueryservice.repository.ShopReviewRepository

@Service
class ShopReviewService(
    private val shopReviewRepository: ShopReviewRepository
) {

    /** reviewId와 reviewTitle을 기반으로 ShopReview를 가져오는 메소드
     * @param reviewId review id
     * @param reviewTitle review title
     * @throws ShopReviewNotFoundException
     * @return ShopReview
     */
    suspend fun findReviewByIdAndTitle(reviewId: String, reviewTitle: String): ShopReview =
        withContext(Dispatchers.IO) {
            val reviewMono = shopReviewRepository.findShopReviewByIdAndTitleWithCaching(reviewId, reviewTitle)
            val reviewDeferred = CoroutinesUtils.monoToDeferred(reviewMono)

            return@withContext reviewDeferred.await() ?: throw ShopReviewNotFoundException("review is not found!!")
        }

    suspend fun getReviewListByShop(shopId: String, shopName: String): List<ShopReview> = withContext(Dispatchers.IO) {
        val reviewFlow = shopReviewRepository.getShopReviewListFlowByShopIdAndNameWithCaching(shopId, shopName)

        // review가 하나도 존재하지 않는 경우에는 예외를 던져버린다
        CoroutinesUtils.monoToDeferred(reviewFlow.first()).await()
            ?: throw ShopReviewNotFoundException("Shop review is not found!!")

        val reviewList = mutableListOf<ShopReview>()

        reviewFlow.buffer()
            .collect {
                val review = CoroutinesUtils.monoToDeferred(it).await()
                reviewList.add(review!!)
            }

        // review가 하나도 안 모였다면 바로 에러 처리
        check(reviewList.size != 0) {
            throw ShopReviewNotFoundException("Shop review is not found!!")
        }

        reviewList
    }
}