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

/**
 * ShopEventHandler
 * shop에 대해서 발행된 event들을 컨슘해서 처리하는 event handler class
 * @param shopCommandService shop의 command logic를 처리하는 useCase layer class
 * @param shopReviewCommandService shopReview command logic을 처리하는 useCase layer class
 * @param shopSearchRepository elasticsearch에 shop을 persist하는데 사용하는 repository class
 */
@Component
class ShopEventHandler(
    private val shopCommandService: ShopCommandService,
    private val shopReviewCommandService: ShopReviewCommandService,
    private val shopSearchRepository: ShopSearchRepository
) {

    @KafkaListener(
        topics = [KafkaTopics.shopCreateTopic],
        groupId = KafkaConsumerGroups.shopGroup
    )
    fun createShop(shop: Shop) {
        shopCommandService.createShop(shop)
            .doOnSuccess { shopSearchRepository.save(it.toSearchEntity()).subscribe() } // 파생 데이터를 elasticsearch로 전송
            .subscribe()
    }

    @KafkaListener(
        topics = [KafkaTopics.shopUpdateTopic],
        groupId = KafkaConsumerGroups.shopGroup
    )
    fun updateShop(updatedEvent: ShopCommand.UpdateRequest) {
        shopCommandService.updateShop(updatedEvent)
            .doOnSuccess { shopSearchRepository.save(it.toSearchEntity()).subscribe() } // record system(dynamoDB)에 persist 성공시 es에 파생데이터 저장
            .subscribe()
    }

    // Shop을 Soft Delete를 수행하는 리스너
    @KafkaListener(
        topics = [KafkaTopics.shopDeleteTopic],
        groupId = KafkaConsumerGroups.shopGroup
    )
    fun deleteShop(deletedEvent: ShopCommand.DeletedEvent) {
        shopCommandService.softDeleteShop(deletedEvent.shopId)
            .doOnNext { shopReviewCommandService.softDeleteAllReviewsOfShop(deletedEvent.shopId).subscribe() } // 연관된 review를 모두 삭제 처리
            .doOnNext { shopSearchRepository.deleteById(it.shopId).subscribe() } // es에 있는 shop data 삭제
            .subscribe()
    }
}