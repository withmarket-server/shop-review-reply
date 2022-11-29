package team.bakkas.domainquery.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import team.bakkas.domainquery.reader.ifs.ShopReviewReader
import team.bakkas.domainquery.service.ifs.ShopReviewQueryService
import team.bakkas.dynamo.shopReview.ShopReview

/**
 * ShopReviewQueryServiceImpl(shopReviewReader: ShopReviewReader)
 * ShopReviewReader의 구현체
 */
@Service
class ShopReviewQueryServiceImpl(
    private val shopReviewReader: ShopReviewReader
) : ShopReviewQueryService {

    /*
     * UseCase layer부터는 coroutine을 적용하여 business logic을 수행한다.
     * Dispatchers.IO를 사용하여 I/O 성능을 높인다.
     */

    override suspend fun findReviewById(reviewId: String): ShopReview? =
        withContext(Dispatchers.IO) {
            val reviewMono = shopReviewReader.findReviewById(reviewId)

            return@withContext reviewMono.awaitSingleOrNull()
        }

    override suspend fun getReviewsByShopId(shopId: String): List<ShopReview> =
        withContext(Dispatchers.IO) {
            val reviewFlow = shopReviewReader.getReviewsByShopId(shopId)

            val reviewList = reviewFlow.toList()

            reviewList
        }
}