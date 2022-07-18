package team.bakkas.applicationquery.service

import kotlinx.coroutines.Dispatchers
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

    suspend fun findReviewByIdAndTitle(reviewId: String, reviewTitle: String): ShopReview = withContext(Dispatchers.IO) {
        val reviewMono = shopReviewRepository.findShopReviewByIdAndTitleWithCaching(reviewId, reviewTitle)
        val reviewDeferred = CoroutinesUtils.monoToDeferred(reviewMono)

        return@withContext reviewDeferred.await() ?: throw ShopReviewNotFoundException("review is not found!!")
    }


}