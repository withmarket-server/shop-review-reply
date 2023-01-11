package team.bakkas.applicationquery.grpc.server

import org.springframework.stereotype.Service
import team.bakkas.domainquery.service.ifs.ReplyQueryService
import team.bakkas.grpcIfs.v1.reply.CheckIsExistReplyRequest
import team.bakkas.grpcIfs.v1.reply.CheckIsExistReplyResponse
import team.bakkas.grpcIfs.v1.reply.ReplyServiceGrpcKt

/**
 * GrpcReplyService(replyQueryService : ReplyQueryService)
 * Reply에 대한 grpc logic을 제공하는 service class
 * @param replyQueryService replyQueryService
 * @since 2022/12/21
 */
@Service
class GrpcReplyService(
    private val replyQueryService: ReplyQueryService
) : ReplyServiceGrpcKt.ReplyServiceCoroutineImplBase() {

    // reviewId 기반으로 reply를 가져온 다음에, replyId와의 일치 여부를 통해서 결과를 반환한다
    override suspend fun isExistReply(request: CheckIsExistReplyRequest): CheckIsExistReplyResponse {
        val reviewId = request.reviewId
        val replyId = request.replyId

        val foundReply = replyQueryService.findByReviewId(reviewId)
        val result = foundReply?.let { it.replyId == replyId } ?: false

        return CheckIsExistReplyResponse.newBuilder()
            .setResult(result)
            .build()
    }
}