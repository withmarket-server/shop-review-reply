package team.bakkas.clientquery.reply

import com.fasterxml.jackson.annotation.JsonProperty

sealed class ReplyQuery {

    data class SimpleResponse(
        @field:JsonProperty("reply_id") var replyId: String,
        @field:JsonProperty("content") var content: String
    )
}
