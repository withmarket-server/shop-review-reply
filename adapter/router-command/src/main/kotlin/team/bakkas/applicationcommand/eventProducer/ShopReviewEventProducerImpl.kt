package team.bakkas.applicationcommand.eventProducer

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import team.bakkas.clientcommand.shop.ShopCommand
import team.bakkas.clientcommand.shopReview.ShopReviewCommand
import team.bakkas.dynamo.shopReview.ShopReview
import team.bakkas.eventinterface.eventProducer.ShopReviewEventProducer
import team.bakkas.eventinterface.kafka.KafkaTopics

/**
 * ShopReviewEventProducerImpl
 * shopReviewEventProducer의 구현체
 * @param shopReviewKafkaTemplate
 * @param reviewDeletedEventKafkaTemplate
 */
@Component
class ShopReviewEventProducerImpl(
    private val shopReviewKafkaTemplate: KafkaTemplate<String, ShopReview>,
    private val reviewDeletedEventKafkaTemplate: KafkaTemplate<String, ShopReviewCommand.DeletedEvent>
): ShopReviewEventProducer {

    override fun propagateCreatedEvent(createdReview: ShopReview): Unit = with(createdReview) {
        shopReviewKafkaTemplate.send(KafkaTopics.shopReviewCreateTopic, this)
    }

    override fun propagateDeletedEvent(deletedEvent: ShopReviewCommand.DeletedEvent): Unit = with(deletedEvent) {
        reviewDeletedEventKafkaTemplate.send(KafkaTopics.shopReviewDeleteTopic, this)
    }
}