package team.bakkas.applicationcommand.grpc

import io.grpc.ManagedChannelBuilder
import org.springframework.stereotype.Component
import team.bakkas.applicationcommand.grpc.ifs.ShopReviewGrpcClient
import team.bakkas.grpcIfs.v1.shopReview.CheckExistShopReviewRequest
import team.bakkas.grpcIfs.v1.shopReview.CheckExistShopReviewResponse
import team.bakkas.grpcIfs.v1.shopReview.ShopReviewServiceGrpcKt

// ShopReview에 대한 gRPC 서비스를 스터빙하는 클래스
@Component
class ShopReviewGrpcClientImpl: ShopReviewGrpcClient {

    companion object {
        // query server를 대상으로 gRPC 포트를 타겟한다
        val channel = ManagedChannelBuilder.forAddress("localhost", 10100)
            .usePlaintext()
            .build()

        // stubbing 객체
        val shopReviewStub = ShopReviewServiceGrpcKt.ShopReviewServiceCoroutineStub(channel)
    }

    /** reviewId, reviewTitle에 대응하는 shopReview가 존재하는지 여부를 반환하는 메소드
     * @param reviewId
     * @param reviewTitle
     * @return CheckExistShopReviewResponse
     */
    override suspend fun isExistShopReview(reviewId: String, reviewTitle: String): CheckExistShopReviewResponse {
        val request = CheckExistShopReviewRequest.newBuilder()
            .setReviewId(reviewId)
            .setReviewTitle(reviewTitle)
            .build()

        return shopReviewStub.isExistShopReview(request)
    }
}