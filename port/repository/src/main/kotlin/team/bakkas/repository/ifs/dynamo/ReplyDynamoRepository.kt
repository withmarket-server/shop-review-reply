package team.bakkas.repository.ifs.dynamo

import reactor.core.publisher.Mono
import team.bakkas.dynamo.reply.Reply

/**
 * ReplyDynamoRepository
 * Reply에 대해서 Dynamo에 접근하는 로직을 정의하는 interface
 */
interface ReplyDynamoRepository {

    fun createReply(reply: Reply): Mono<Reply>

    fun findByReviewId(reviewId: String): Mono<Reply>
}