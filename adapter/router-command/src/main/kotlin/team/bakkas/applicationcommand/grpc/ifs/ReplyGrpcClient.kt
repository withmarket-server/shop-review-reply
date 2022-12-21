package team.bakkas.applicationcommand.grpc.ifs

import team.bakkas.grpcIfs.v1.reply.CheckIsExistReplyResponse

/**
 * ReplyGrpcClient
 * reply에 대해서 grpc server를 stub하는 client class
 * @since 2022/12/21
 */
interface ReplyGrpcClient {

    suspend fun isExistReply(reviewId: String, replyId: String) : CheckIsExistReplyResponse
}