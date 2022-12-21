package team.bakkas.clientcommand.reply

import com.fasterxml.jackson.annotation.JsonProperty
import team.bakkas.clientcommand.reply.annotations.ReplyCreatable
import team.bakkas.clientcommand.reply.annotations.ReplyDeletable
import java.util.UUID


sealed class ReplyCommand {

    // Reply 생성 요청과 이벤트를 담당하는 dto class
    @ReplyCreatable
    data class CreateRequest(
        @field:JsonProperty("member_id") var memberId: String,
        @field:JsonProperty("shop_id") var shopId: String,
        @field:JsonProperty("review_id") var reviewId: String,
        @field:JsonProperty("content") var content: String
    ) {
        fun transformToEvent() = CreatedEvent(
            replyId = UUID.randomUUID().toString(),
            shopId = shopId,
            reviewId = reviewId,
            content = content
        )
    }

    @ReplyDeletable
    data class DeleteRequest(
        @field:JsonProperty("shop_id") var shopId: String,
        @field:JsonProperty("review_id") var reviewId: String,
        @field:JsonProperty("reply_id") var replyId: String,
        @field:JsonProperty("member_id") var memberId: String
    ) {
        companion object {
            fun of(shopId: String, reviewId: String, replyId: String, memberId: String): DeleteRequest {
                return DeleteRequest(shopId, reviewId, replyId, memberId)
            }
        }

        // DeletedEvent를 발행해주는 메소드
        fun transformToEvent(): DeletedEvent {
            return DeletedEvent(this.reviewId, this.replyId)
        }
    }

    // 답글 생성 이벤트
    data class CreatedEvent(
        var replyId: String,
        var shopId: String,
        var reviewId: String,
        var content: String
    )

    // 답글 삭제 이벤트
    data class DeletedEvent(
        var reviewId: String,
        var replyId: String
    )
}