package team.bakkas.applicationquery.grpc

import org.springframework.stereotype.Service
import team.bakkas.domainquery.service.ifs.ShopReviewQueryService
import team.bakkas.grpcIfs.v1.shopReview.CheckExistShopReviewRequest
import team.bakkas.grpcIfs.v1.shopReview.CheckExistShopReviewResponse
import team.bakkas.grpcIfs.v1.shopReview.ShopReviewServiceGrpcKt

@Service
class GrpcShopReviewService(
    private val shopReviewQueryService: ShopReviewQueryService
): ShopReviewServiceGrpcKt.ShopReviewServiceCoroutineImplBase() {

    /** Shop이 존재하는지 검증해주는 메소드
     * @param request reviewId, reviewTitle이 포함된 parameter
     * @throws ShopReviewNotFoundException
     * @return CheckExistShopReviewResponse
     */
    override suspend fun isExistShopReview(request: CheckExistShopReviewRequest): CheckExistShopReviewResponse {
        val reviewId = request.reviewId
        val reviewTitle = request.reviewTitle

        // 찾으면 예외 발생이 없음
        val foundReview = shopReviewQueryService.findReviewByIdAndTitle(reviewId, reviewTitle)

        return when(foundReview) {
            null -> CheckExistShopReviewResponse.newBuilder()
                .setResult(false)
                .build()
            else -> CheckExistShopReviewResponse.newBuilder()
                .setResult(true)
                .build()
        }
    }
}