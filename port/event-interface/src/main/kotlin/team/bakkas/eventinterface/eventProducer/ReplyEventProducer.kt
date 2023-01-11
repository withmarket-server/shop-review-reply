package team.bakkas.eventinterface.eventProducer

import team.bakkas.clientcommand.reply.ReplyCommand

/**
 * ReplyEventProducer
 * Reply에 대한 이벤트를 발행하는 역할을 담당하는 클래스
 * @since 2022/12/04
 */
interface ReplyEventProducer {

    fun propagateReplyCreated(createdEvent: ReplyCommand.CreatedEvent)

    fun propagateReplyDeleted(deletedEvent: ReplyCommand.DeletedEvent)
}