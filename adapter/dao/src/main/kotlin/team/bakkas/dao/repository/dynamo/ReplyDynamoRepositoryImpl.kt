package team.bakkas.dao.repository.dynamo

import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.reactive.asFlow
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import team.bakkas.dynamo.reply.Reply
import team.bakkas.dynamo.reply.extentions.softDelete
import team.bakkas.repository.ifs.dynamo.ReplyDynamoRepository

/**
 * ReplyDynamoRepositoryImpl
 * ReplyDynamoRepository의 구현체
 * @param dynamoDbEnhancedAsyncClient dynamo enhanced client
 */
@Repository
class ReplyDynamoRepositoryImpl(
    private val dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient
) : ReplyDynamoRepository {

    private val asyncTable = dynamoDbEnhancedAsyncClient.table("reply", Reply.tableSchema)

    override fun createReply(reply: Reply): Mono<Reply> {
        return asyncTable.putItem(reply)
            .toMono()
            .thenReturn(reply)
    }

    override fun findByReviewId(reviewId: String): Mono<Reply> {
        return asyncTable.scan { it.filterExpression(generateReplyExpression(reviewId)) }
            .items()
            .toFlux()
            .singleOrEmpty() // 하나만 가져오거나, 아니면 empty mono를 가져온다
    }

    override fun findById(replyId: String): Mono<Reply> {
        val replyKey = generateKey(replyId)

        return asyncTable.getItem(replyKey)
            .toMono()
    }

    override fun softDeleteById(replyId: String): Mono<Reply> {
        return findById(replyId)
            .map { it.softDelete() } // soft delete를 적용한 후
            .flatMap { createReply(it) } // record system에 다시 저장
    }

    private fun generateKey(replyId: String): Key {
        return Key.builder()
            .partitionValue(replyId)
            .build()
    }

    private fun generateReplyExpression(reviewId: String): Expression {
        val attributeAliasMap = mutableMapOf<String, String>()
        val attributeValueMap = mutableMapOf<String, AttributeValue>()

        attributeAliasMap["#review_id"] = "review_id"

        attributeValueMap[":id_val"] = AttributeValue.fromS(reviewId)

        return Expression.builder()
            .expressionNames(attributeAliasMap)
            .expressionValues(attributeValueMap)
            .expression("#review_id = :id_val")
            .build()
    }
}