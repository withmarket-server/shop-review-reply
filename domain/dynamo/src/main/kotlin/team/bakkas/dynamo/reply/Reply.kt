package team.bakkas.dynamo.reply

import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import team.bakkas.dynamo.BaseTimeEntity
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

/** 가게의 Review에 대한 사장님의 답글 테이블 엔티티를 정의하는 클래스
 * @author Brian
 * @since 2022/10/29
 */
@DynamoDbBean
class Reply(
    var replyId: String = UUID.randomUUID().toString(),
    var reviewId: String = "",
    var content: String = ""
): Serializable, BaseTimeEntity() {
    companion object {
        // Reply dynamo table의 review에 대한 인덱스
        val reviewIndexName = ""

        // Review에 대한 사장님의 Reply의 table schema
        val tableSchema = TableSchema.builder(Reply::class.java)
            .newItemSupplier(::Reply)
            .addAttribute(String::class.java) {
                it.name("reply_id").getter(Reply::replyId::get)
                    .setter(Reply::replyId::set)
                    .tags(StaticAttributeTags.primaryPartitionKey())
            }
            .addAttribute(String::class.java) {
                it.name("review_id").getter(Reply::reviewId::get)
                    .setter(Reply::reviewId::set)
                    .tags(StaticAttributeTags.secondaryPartitionKey(reviewIndexName))
            }
            .addAttribute(String::class.java) {
                it.name("content").getter(Reply::content::get)
                    .setter(Reply::content::set)
            }
            .addAttribute(LocalDateTime::class.java) {
                it.name("created_at").getter(Reply::createdAt::get)
                    .setter(Reply::createdAt::set)
            }
            .addAttribute(LocalDateTime::class.java) {
                it.name("deleted_at").getter(Reply::deletedAt::get)
                    .setter(Reply::deletedAt::set)
            }
            .build()
    }
}