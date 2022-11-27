package team.bakkas.applicationcommand.eventProducer

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import team.bakkas.clientcommand.shop.ShopCommand
import team.bakkas.dynamo.shop.Shop
import team.bakkas.eventinterface.eventProducer.ShopEventProducer
import team.bakkas.eventinterface.kafka.KafkaTopics

/**
 * ShopEventProducerImpl
 * ShopEventProducer의 구현체
 * @param shopKafkaTemplate
 * @param shopUpdatedEventKafkaTemplate
 * @param shopDeletedEventKafkaTemplate
 */
@Component
class ShopEventProducerImpl(
    private val shopKafkaTemplate: KafkaTemplate<String, Shop>,
    private val shopUpdatedEventKafkaTemplate: KafkaTemplate<String, ShopCommand.UpdateRequest>,
    private val shopDeletedEventKafkaTemplate: KafkaTemplate<String, ShopCommand.DeletedEvent>
) : ShopEventProducer {

    override fun propagateShopCreated(shop: Shop): Unit = with(shop) {
        shopKafkaTemplate.send(KafkaTopics.shopCreateTopic, this)
    }

    override fun propagateShopUpdated(updatedEvent: ShopCommand.UpdateRequest): Unit = with(updatedEvent) {
        shopUpdatedEventKafkaTemplate.send(KafkaTopics.shopUpdateTopic, this)
    }

    override fun propagateShopDeleted(deletedEvent: ShopCommand.DeletedEvent): Unit = with(deletedEvent) {
        shopDeletedEventKafkaTemplate.send(KafkaTopics.shopDeleteTopic, this)
    }
}