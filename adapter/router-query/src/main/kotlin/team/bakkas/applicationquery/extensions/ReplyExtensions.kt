package team.bakkas.applicationquery.extensions

import team.bakkas.clientquery.reply.ReplyQuery
import team.bakkas.dynamo.reply.Reply

fun Reply.toSimpleResponse() = ReplyQuery.SimpleResponse(
    replyId = this.replyId,
    content = this.content
)