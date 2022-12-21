package team.bakkas.applicationcommand.grpc

import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import team.bakkas.applicationcommand.grpc.ifs.ReplyGrpcClient
import team.bakkas.grpcIfs.v1.reply.CheckIsExistReplyRequest
import team.bakkas.grpcIfs.v1.reply.CheckIsExistReplyResponse
import team.bakkas.grpcIfs.v1.reply.ReplyServiceGrpcKt

/**
 * ReplyGrpcClientImpl
 * ReplyGrpcClient를 구현하는 component class
 * @since 2022/12/21
 */
@Component
class ReplyGrpcClientImpl(
    @Value("\${grpc.shop-query}") private val channelHost: String
) : ReplyGrpcClient {

    private val channel = ManagedChannelBuilder.forAddress(channelHost, 10100)
        .usePlaintext()
        .build()

    private val replyStub = ReplyServiceGrpcKt.ReplyServiceCoroutineStub(channel)

    override suspend fun isExistReply(reviewId: String, replyId: String): CheckIsExistReplyResponse {
        val request = CheckIsExistReplyRequest.newBuilder()
            .setReviewId(reviewId)
            .setReplyId(replyId)
            .build()

        return replyStub.isExistReply(request)
    }
}