package team.bakkas.applicationkafka.eventListeners

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import team.bakkas.dynamo.shop.Shop
import team.bakkas.eventinterface.kafka.KafkaConsumerGroups
import team.bakkas.eventinterface.kafka.KafkaTopics
import team.bakkas.servicecommand.service.ifs.ShopCommandService

// Kafka에 발행된 shop관련 메시지를 redis에 캐싱하는 리스너를 정의하는 클래스
@Component
class ShopEventListener(
    private val shopCommandService: ShopCommandService
) {

    // withmarket.shop.create 토픽에 있는 메시지를 읽어내서 shop을 dynamo, redis에 저장하는 메소드
    @KafkaListener(
        topics = [KafkaTopics.shopCreateTopic],
        groupId = KafkaConsumerGroups.shopGroup
    )
    fun createShopToDynamo(shop: Shop) {
        shopCommandService.createShop(shop).subscribe()
    }
}