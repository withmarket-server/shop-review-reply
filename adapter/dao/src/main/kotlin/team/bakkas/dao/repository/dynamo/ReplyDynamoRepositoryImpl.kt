package team.bakkas.dao.repository.dynamo

import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import team.bakkas.dynamo.reply.Reply
import team.bakkas.repository.ifs.dynamo.ReplyDynamoRepository

/**
 * ReplyDynamoRepositoryImpl
 * ReplyDynamoRepository의 구현체
 * @param dynamoDbEnhancedAsyncClient dynamo enhanced client
 */
@Repository
class ReplyDynamoRepositoryImpl(
    private val dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient
): ReplyDynamoRepository {

    private val asyncTable = dynamoDbEnhancedAsyncClient.table("reply", Reply.tableSchema)

    override fun createReply(reply: Reply): Mono<Reply> {
        return asyncTable.putItem(reply)
            .toMono()
            .thenReturn(reply)
    }
}