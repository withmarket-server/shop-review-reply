package team.bakkas.domainquery.reader

import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import team.bakkas.domainquery.reader.ifs.ReplyReader
import team.bakkas.dynamo.reply.Reply
import team.bakkas.repository.ifs.dynamo.ReplyDynamoRepository
import team.bakkas.repository.ifs.redis.ReplyRedisRepository

/**
 * ReplyReaderImpl
 * ReplyReader의 구현체. facade pattern 구현체이다.
 * @since 2022/12/16
 */
@Repository
class ReplyReaderImpl(
    private val replyDynamoRepository: ReplyDynamoRepository,
    private val replyRedisRepository: ReplyRedisRepository
) : ReplyReader {

    override fun findByReviewId(reviewId: String): Mono<Reply> {
        val alternativeMono = replyDynamoRepository.findByReviewId(reviewId)
            .doOnSuccess { replyRedisRepository.cacheReply(it).subscribe() }
            .onErrorResume { Mono.empty() }

        return replyRedisRepository.findByReviewId(reviewId)
            .switchIfEmpty(alternativeMono)
    }
}