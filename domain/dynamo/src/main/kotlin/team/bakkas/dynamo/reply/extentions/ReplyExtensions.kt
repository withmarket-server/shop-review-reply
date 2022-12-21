package team.bakkas.dynamo.reply.extentions

import team.bakkas.dynamo.reply.Reply
import java.time.LocalDateTime

// 해당 Reply를 record system 상에서 soft delete를 적용하는 메소드
fun Reply.softDelete(): Reply {
    this.deletedAt = LocalDateTime.now()

    return this
}