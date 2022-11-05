package team.bakkas.domainquery.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.bakkas.common.exceptions.shopReview.ShopReviewNotFoundException
import team.bakkas.domainquery.reader.ifs.ShopReviewReader
import team.bakkas.domainquery.service.ifs.ShopReviewQueryService
import team.bakkas.dynamo.shopReview.ShopReview

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
    override suspend fun findReviewById(reviewId: String): ShopReview? =
        withContext(Dispatchers.IO) {
            val reviewMono = shopReviewReader.findReviewById(reviewId)

            return@withContext reviewMono.awaitSingleOrNull()
        }

    @Transactional(readOnly = true)
    override suspend fun getReviewsByShopId(shopId: String): List<ShopReview> =
        withContext(Dispatchers.IO) {
            val reviewFlow = shopReviewReader.getReviewsByShopId(shopId)

            val reviewList = reviewFlow.toList()

            reviewList
        }
}