package team.bakkas.applicationquery.grpc.server

import org.springframework.stereotype.Service
import team.bakkas.domainquery.service.ifs.ShopReviewQueryService
import team.bakkas.grpcIfs.v1.shopReview.*

/**
 * GrpcShopReviewService
 * shopReview에 대한 grpc server 기능을 제공하는 grpc service class
 * @param shopReviewQueryService
 */
@Service
class GrpcShopReviewService(
    private val shopReviewQueryService: ShopReviewQueryService
) : ShopReviewServiceGrpcKt.ShopReviewServiceCoroutineImplBase() {

    override suspend fun isExistShopReview(request: CheckExistShopReviewRequest): CheckExistShopReviewResponse {
        val reviewId = request.reviewId

        // 찾으면 예외 발생이 없음
        val foundReview = shopReviewQueryService.findReviewById(reviewId)
        val isSatisfied = foundReview != null

        return CheckExistShopReviewResponse.newBuilder()
            .setResult(isSatisfied)
            .build()
    }

    override suspend fun isRepliedReview(request: CheckIsRepliedReviewRequest): CheckIsRepliedReviewResponse {
        val reviewId = request.reviewId

        val foundReview = shopReviewQueryService.findReviewById(reviewId)

        val isExists = foundReview != null
        val isReplied = foundReview?.isReplyExists ?: false

        return CheckIsRepliedReviewResponse.newBuilder()
            .setIsExists(isExists)
            .setIsReplied(isReplied)
            .build()
    }
}