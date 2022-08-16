package team.bakkas.domainqueryservice.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.springframework.core.CoroutinesUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.bakkas.common.exceptions.shopReview.ShopReviewNotFoundException
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domainqueryservice.repository.ifs.ShopReviewReader
import team.bakkas.domainqueryservice.service.ifs.ShopReviewQueryService

@Service
class ShopReviewQueryServiceImpl(
    private val shopReviewReader: ShopReviewReader
) : ShopReviewQueryService {

    /** reviewId와 reviewTitle을 기반으로 ShopReview를 가져오는 메소드
     * @param reviewId review id
     * @param reviewTitle review title
     * @throws ShopReviewNotFoundException
     * @return ShopReview
     */
    @Transactional(readOnly = true)
    override suspend fun findReviewByIdAndTitle(reviewId: String, reviewTitle: String): ShopReview =
        withContext(Dispatchers.IO) {
            val reviewMono = shopReviewReader.findShopReviewByIdAndTitleWithCaching(reviewId, reviewTitle)
            val reviewDeferred = CoroutinesUtils.monoToDeferred(reviewMono)

            return@withContext reviewDeferred.await() ?: throw ShopReviewNotFoundException("review is not found!!")
        }

    @Transactional(readOnly = true)
    override suspend fun getReviewListByShop(shopId: String, shopName: String): List<ShopReview> =
        withContext(Dispatchers.IO) {
            val reviewFlow = shopReviewReader.getShopReviewListFlowByShopIdAndNameWithCaching(shopId, shopName)

            // flow에 item이 하나도 전달이 안 되는 경우의 예외 처리
            checkNotNull(reviewFlow.firstOrNull()) {
                throw ShopReviewNotFoundException("Shop review is not found!!")
            }

            val reviewList = reviewFlow.toList()

            reviewList
        }
}