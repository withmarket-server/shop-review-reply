package team.bakkas.applicationcommand.eventProducer

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import team.bakkas.clientcommand.reply.ReplyCommand
import team.bakkas.eventinterface.eventProducer.ReplyEventProducer
import team.bakkas.eventinterface.kafka.KafkaTopics

/**
 * ReplyEventProducerImpl
 * ReplyEventProducer의 구현체
 * @param replyCreatedEventKafkaTemplate reply 생성 이벤트를 발행하는 kafkaTemplate
 * @since 2022/12/04
 */
@Component
class ReplyEventProducerImpl(
    private val replyCreatedEventKafkaTemplate: KafkaTemplate<String, ReplyCommand.CreatedEvent>,
    private val replyDeletedEventKafkaTemplate: KafkaTemplate<String, ReplyCommand.DeletedEvent>
) : ReplyEventProducer {

    override fun propagateReplyCreated(createdEvent: ReplyCommand.CreatedEvent): Unit = with(createdEvent) {
        replyCreatedEventKafkaTemplate.send(KafkaTopics.replyCreateTopic, this)
    }

    override fun propagateReplyDeleted(deletedEvent: ReplyCommand.DeletedEvent): Unit = with(deletedEvent) {
        replyDeletedEventKafkaTemplate.send(KafkaTopics.replyDeleteTopic, this)
    }
}