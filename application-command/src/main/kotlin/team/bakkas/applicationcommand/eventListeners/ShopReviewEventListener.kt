package team.bakkas.applicationcommand.eventListeners

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import team.bakkas.applicationcommand.kafka.KafkaConsumerGroups
import team.bakkas.applicationcommand.kafka.KafkaTopics
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domaindynamo.repository.redis.ShopReviewRedisRepository

// TODO 별도의 어플리케이션으로 분리해야함
// kafka로부터 이벤트를 구독하여 shop review에 대해 redis로 캐싱하는 로직을 정의하는 component
@Component
class ShopReviewEventListener(
    private val shopReviewRedisRepository: ShopReviewRedisRepository
) {
    // review 생성 이벤트가 발행되면 처리하는 메소드
    @KafkaListener(topics = [KafkaTopics.shopReviewCreateTopic], groupId = KafkaConsumerGroups.createShopReviewGroup)
    fun cacheCreatedShop(shopReview: ShopReview) {
        shopReviewRedisRepository.cacheReview(shopReview)
            .subscribe()
    }
}