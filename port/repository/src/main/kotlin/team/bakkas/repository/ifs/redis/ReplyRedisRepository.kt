package team.bakkas.repository.ifs.redis

import reactor.core.publisher.Mono
import team.bakkas.dynamo.reply.Reply

/**
 * ReplyRedisRepository
 * Reply에 대해서 Redis에 접근하는 로직을 정의하는 interface
 */
interface ReplyRedisRepository {

    fun cacheReply(reply: Reply): Mono<Reply>

    fun findByReviewId(reviewId: String): Mono<Reply>

    fun deleteReply(reviewId: String, replyId: String): Mono<Boolean>
}