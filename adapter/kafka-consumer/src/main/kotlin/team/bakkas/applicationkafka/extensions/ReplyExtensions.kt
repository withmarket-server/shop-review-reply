package team.bakkas.applicationkafka.extensions

import team.bakkas.clientcommand.reply.ReplyCommand
import team.bakkas.dynamo.reply.Reply


fun ReplyCommand.CreatedEvent.toEntity() = Reply(
    replyId = replyId,
    reviewId = reviewId,
    content = content
)