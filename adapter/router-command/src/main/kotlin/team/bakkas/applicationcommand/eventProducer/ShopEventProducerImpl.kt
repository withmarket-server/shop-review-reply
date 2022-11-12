package team.bakkas.applicationcommand.eventProducer

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import team.bakkas.clientcommand.shop.ShopCommand
import team.bakkas.dynamo.shop.Shop
import team.bakkas.eventinterface.eventProducer.ShopEventProducer
import team.bakkas.eventinterface.kafka.KafkaTopics

// Shop에 대한 event를 produce하는 로직을 보관하는 클래스
@Component
class ShopEventProducerImpl(
    private val shopKafkaTemplate: KafkaTemplate<String, Shop>,
    private val shopUpdatedEventKafkaTemplate: KafkaTemplate<String, ShopCommand.UpdateRequest>,
    private val shopDeletedEventKafkaTemplate: KafkaTemplate<String, ShopCommand.DeletedEvent>
) : ShopEventProducer {

    // Kafka에다가 생성된 shop을 메시지로 전송하여 consume하는 쪽에서 redis에 캐싱하도록 구현한다
    override fun propagateShopCreated(shop: Shop): Unit = with(shop) {
        shopKafkaTemplate.send(KafkaTopics.shopCreateTopic, this)
    }

    override fun propagateShopUpdated(updatedEvent: ShopCommand.UpdateRequest): Unit = with(updatedEvent) {
        shopUpdatedEventKafkaTemplate.send(KafkaTopics.shopUpdateTopic, this)
    }

    // Kafka에다가 shopDeletedEvent를 전송하여 consume하는 쪽에서 처리한다
    override fun propagateShopDeleted(deletedEvent: ShopCommand.DeletedEvent): Unit = with(deletedEvent) {
        shopDeletedEventKafkaTemplate.send(KafkaTopics.shopDeleteTopic, this)
    }
}