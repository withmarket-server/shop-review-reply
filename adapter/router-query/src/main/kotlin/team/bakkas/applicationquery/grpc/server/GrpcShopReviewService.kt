package team.bakkas.applicationquery.grpc.server

import org.springframework.stereotype.Service
import team.bakkas.domainquery.service.ifs.ShopReviewQueryService
import team.bakkas.grpcIfs.v1.shopReview.CheckExistShopReviewRequest
import team.bakkas.grpcIfs.v1.shopReview.CheckExistShopReviewResponse
import team.bakkas.grpcIfs.v1.shopReview.ShopReviewServiceGrpcKt

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
        val isSatisfied = foundReview != null && foundReview.deletedAt == null

        return CheckExistShopReviewResponse.newBuilder()
            .setResult(isSatisfied)
            .build()
    }
}