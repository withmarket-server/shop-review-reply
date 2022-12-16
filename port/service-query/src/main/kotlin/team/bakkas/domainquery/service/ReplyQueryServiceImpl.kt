package team.bakkas.domainquery.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import team.bakkas.domainquery.reader.ifs.ReplyReader
import team.bakkas.domainquery.service.ifs.ReplyQueryService
import team.bakkas.dynamo.reply.Reply

/**
 * ReplyQueryServiceImpl
 * @since 2022/12/16
 */
@Service
class ReplyQueryServiceImpl(
    private val replyReader: ReplyReader
) : ReplyQueryService {

    override suspend fun findByReviewId(reviewId: String): Reply? = withContext(Dispatchers.IO) {
        replyReader.findByReviewId(reviewId).awaitSingleOrNull()
    }
}