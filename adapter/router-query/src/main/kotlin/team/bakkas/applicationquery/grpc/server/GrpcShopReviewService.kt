package team.bakkas.applicationquery.grpc.server

import org.springframework.stereotype.Service
import team.bakkas.domainquery.service.ifs.ShopReviewQueryService
import team.bakkas.grpcIfs.v1.shopReview.CheckExistShopReviewRequest
import team.bakkas.grpcIfs.v1.shopReview.CheckExistShopReviewResponse
import team.bakkas.grpcIfs.v1.shopReview.ShopReviewServiceGrpcKt

@Service
class GrpcShopReviewService(
    private val shopReviewQueryService: ShopReviewQueryService
) : ShopReviewServiceGrpcKt.ShopReviewServiceCoroutineImplBase() {

    /** Shop이 존재하는지 검증해주는 메소드
     * @param request reviewId, reviewTitle이 포함된 parameter
     * @throws ShopReviewNotFoundException
     * @return CheckExistShopReviewResponse
     */
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