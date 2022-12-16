package team.bakkas.domainquery.reader.ifs

import reactor.core.publisher.Mono
import team.bakkas.dynamo.reply.Reply

/**
 * ReplyReader
 * Reply에 대한 query facade
 * @since 2022/12/16
 */
interface ReplyReader {

    fun findByReviewId(reviewId: String): Mono<Reply>
}