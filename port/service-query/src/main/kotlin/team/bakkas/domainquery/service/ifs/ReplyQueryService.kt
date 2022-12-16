package team.bakkas.domainquery.service.ifs

import team.bakkas.dynamo.reply.Reply

/**
 * ReplyQueryService
 * Reply에 대한 Query business logic을 처리하는 service interface
 * Clean Architecture에서 UseCase layer에 대응한다.
 * @since 2022/12/16
 */
interface ReplyQueryService {

    suspend fun findByReviewId(reviewId: String): Reply?
}