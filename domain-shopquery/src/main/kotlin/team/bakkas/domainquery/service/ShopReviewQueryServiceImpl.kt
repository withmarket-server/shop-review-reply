package team.bakkas.domainquery.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.springframework.core.CoroutinesUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.bakkas.common.exceptions.shopReview.ShopReviewNotFoundException
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domainquery.repository.ifs.ShopReviewReader
import team.bakkas.domainquery.service.ifs.ShopReviewQueryService

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
            val reviewFlow = shopReviewReader.getReviewFlowByShopIdAndName(shopId, shopName)

            val reviewList = reviewFlow.toList()

            reviewList
        }
}