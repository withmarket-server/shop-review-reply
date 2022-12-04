package team.bakkas.clientcommand.reply

import com.fasterxml.jackson.annotation.JsonProperty
import team.bakkas.clientcommand.reply.annotations.ReplyCreatable


sealed class ReplyCommand {

    // Reply 생성 요청과 이벤트를 담당하는 dto class
    @ReplyCreatable
    data class CreateRequest(
        @field:JsonProperty("member_id") var memberId: String,
        @field:JsonProperty("shop_id") var shopId: String,
        @field:JsonProperty("review_id") var reviewId: String,
        @field:JsonProperty("content") var content: String
    )
}