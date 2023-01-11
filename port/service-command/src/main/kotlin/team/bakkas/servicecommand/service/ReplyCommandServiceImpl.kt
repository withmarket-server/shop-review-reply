package team.bakkas.servicecommand.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import team.bakkas.dynamo.reply.Reply
import team.bakkas.repository.ifs.dynamo.ReplyDynamoRepository
import team.bakkas.repository.ifs.redis.ReplyRedisRepository
import team.bakkas.servicecommand.service.ifs.ReplyCommandService

/**
 * ReplyCommandServiceImpl
 * ReplyCommandService의 구현체
 * @since 2022/12/05
 */
@Service
class ReplyCommandServiceImpl(
    private val replyDynamoRepository: ReplyDynamoRepository,
    private val replyRedisRepository: ReplyRedisRepository
) : ReplyCommandService {

    override fun createReply(reply: Reply): Mono<Reply> {
        return replyDynamoRepository.createReply(reply) // reply를 dynamo에 기록하고
            .doOnSuccess { replyRedisRepository.cacheReply(reply).subscribe() } // dynamo에 기록을 성공할 시 redis에도 전파
    }

    override fun deleteById(replyId: String): Mono<Reply> {
        return replyDynamoRepository.softDeleteById(replyId)
            .doOnSuccess { replyRedisRepository.deleteReply(it.reviewId, it.replyId).subscribe() }
    }
}