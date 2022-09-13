package team.bakkas.domainshopcommand.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domainshopcommand.service.ifs.ShopReviewCommandService
import team.bakkas.repository.ifs.dynamo.ShopReviewDynamoRepository

@Service
class ShopReviewCommandServiceImpl(
    private val shopReviewDynamoRepository: ShopReviewDynamoRepository
) : ShopReviewCommandService {

    @Transactional
    override suspend fun createReview(shopReview: ShopReview): ShopReview = withContext(Dispatchers.IO) {
        // 검증이 끝나면 review 생성
        shopReviewDynamoRepository.createReviewAsync(shopReview).awaitSingle()

        return@withContext shopReview
    }

    @Transactional
    override suspend fun deleteReview(reviewId: String, reviewTitle: String): ShopReview = withContext(Dispatchers.IO) {
        // 검증이 끝나면 review 삭제
        val deletedReview = shopReviewDynamoRepository.deleteReviewAsync(reviewId, reviewTitle).awaitSingle()

        return@withContext deletedReview
    }
}