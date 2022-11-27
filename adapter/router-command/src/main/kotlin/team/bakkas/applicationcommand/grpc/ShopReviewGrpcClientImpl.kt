package team.bakkas.applicationcommand.grpc

import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import team.bakkas.applicationcommand.grpc.ifs.ShopReviewGrpcClient
import team.bakkas.grpcIfs.v1.shopReview.CheckExistShopReviewRequest
import team.bakkas.grpcIfs.v1.shopReview.CheckExistShopReviewResponse
import team.bakkas.grpcIfs.v1.shopReview.ShopReviewServiceGrpcKt

/**
 * ShopReviewGrpcClientImpl
 * ShopReviewGrpcClient의 구현체
 * @param channelHost grpc server's channel host
 */
@Component
class ShopReviewGrpcClientImpl(
    @Value("\${grpc.shop-query}") private val channelHost: String
) : ShopReviewGrpcClient {

    private val channel = ManagedChannelBuilder.forAddress(channelHost, 10100)
        .usePlaintext()
        .build()

    private val shopReviewStub = ShopReviewServiceGrpcKt.ShopReviewServiceCoroutineStub(channel)

    override suspend fun isExistShopReview(reviewId: String): CheckExistShopReviewResponse {
        val request = CheckExistShopReviewRequest.newBuilder()
            .setReviewId(reviewId)
            .build()

        return shopReviewStub.isExistShopReview(request)
    }
}