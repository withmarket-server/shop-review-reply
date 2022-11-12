package team.bakkas.applicationkafka.eventHandlers

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import team.bakkas.applicationkafka.extensions.toSearchEntity
import team.bakkas.clientcommand.shop.ShopCommand
import team.bakkas.dynamo.shop.Shop
import team.bakkas.elasticsearch.repository.ShopSearchRepository
import team.bakkas.eventinterface.kafka.KafkaConsumerGroups
import team.bakkas.eventinterface.kafka.KafkaTopics
import team.bakkas.servicecommand.service.ifs.ShopCommandService
import team.bakkas.servicecommand.service.ifs.ShopReviewCommandService

// Kafka에 발행된 shop관련 메시지를 redis에 캐싱하는 리스너를 정의하는 클래스
@Component
class ShopEventHandler(
    private val shopCommandService: ShopCommandService,
    private val shopReviewCommandService: ShopReviewCommandService,
    private val shopSearchRepository: ShopSearchRepository
) {

    // withmarket.shop.create 토픽에 있는 메시지를 읽어내서 shop을 dynamo, redis에 저장하는 메소드
    @KafkaListener(
        topics = [KafkaTopics.shopCreateTopic],
        groupId = KafkaConsumerGroups.shopGroup
    )
    fun createShop(shop: Shop) {
        shopCommandService.createShop(shop)
            .doOnSuccess { shopSearchRepository.save(it.toSearchEntity()).subscribe() }
            .subscribe()
    }

    @KafkaListener(
        topics = [KafkaTopics.shopUpdateTopic],
        groupId = KafkaConsumerGroups.shopGroup
    )
    fun updateShop(updatedEvent: ShopCommand.UpdateRequest) {
        shopCommandService.updateShop(updatedEvent)
            .doOnSuccess { shopSearchRepository.save(it.toSearchEntity()).subscribe() }
            .subscribe()
    }

    // Shop을 Soft Delete를 수행하는 리스너
    @KafkaListener(
        topics = [KafkaTopics.shopDeleteTopic],
        groupId = KafkaConsumerGroups.shopGroup
    )
    fun deleteShop(deletedEvent: ShopCommand.DeletedEvent) {
        shopCommandService.softDeleteShop(deletedEvent.shopId)
            .doOnNext { shopReviewCommandService.softDeleteAllReviewsOfShop(deletedEvent.shopId).subscribe() }
            .doOnNext { shopSearchRepository.deleteById(it.shopId).subscribe() }
            .subscribe()
    }
}