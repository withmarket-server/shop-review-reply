package team.bakkas.applicationcommand.grpc.ifs

import team.bakkas.grpcIfs.v1.shopReview.CheckExistShopReviewResponse
import team.bakkas.grpcIfs.v1.shopReview.CheckIsRepliedReviewResponse

// ShopReview에 대한 gRPC stubbing을 담당하는 인터페이스
interface ShopReviewGrpcClient {

    suspend fun isExistShopReview(reviewId: String): CheckExistShopReviewResponse

    suspend fun isRepliedReview(reviewId: String): CheckIsRepliedReviewResponse
}