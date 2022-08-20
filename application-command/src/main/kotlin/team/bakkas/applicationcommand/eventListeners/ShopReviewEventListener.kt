package team.bakkas.applicationcommand.eventListeners

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import team.bakkas.domainkafka.kafka.KafkaConsumerGroups
import team.bakkas.domainkafka.kafka.KafkaTopics
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domaindynamo.repository.redis.ShopReviewRedisRepository

// TODO 별도의 어플리케이션으로 분리해야함
// kafka로부터 이벤트를 구독하여 shop review에 대해 redis로 캐싱하는 로직을 정의하는 component
@Component
class ShopReviewEventListener(
    private val shopReviewRedisRepository: ShopReviewRedisRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    // review 생성 이벤트가 발행되면 처리하는 메소드
    @KafkaListener(topics = [KafkaTopics.shopReviewCreateTopic], groupId = KafkaConsumerGroups.createShopReviewGroup)
    fun cacheCreatedShopReview(shopReview: ShopReview) {
        shopReviewRedisRepository.cacheReview(shopReview)
            .subscribe()
    }

    // review 삭제 이벤트가 발행되면 처리하는 메소드
    @KafkaListener(topics = [KafkaTopics.shopReviewDeleteTopic], groupId = KafkaConsumerGroups.deleteShopReviewGroup)
    fun deleteCache(shopReview: ShopReview) {
        shopReviewRedisRepository.deleteReview(shopReview)
            .doOnError { logger.info("(Delete review cache) Cache not exist!") }
            .subscribe()
    }
}