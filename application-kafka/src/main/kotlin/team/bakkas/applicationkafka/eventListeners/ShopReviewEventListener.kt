package team.bakkas.applicationkafka.eventListeners

import kotlinx.coroutines.reactor.asFlux
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import team.bakkas.clientquery.dto.ShopReviewQuery
import team.bakkas.domainkafka.kafka.KafkaConsumerGroups
import team.bakkas.domainkafka.kafka.KafkaTopics
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domaindynamo.repository.dynamo.ShopReviewDynamoRepository
import team.bakkas.domaindynamo.repository.redis.ShopReviewRedisRepository

// TODO 별도의 어플리케이션으로 분리해야함
// kafka로부터 이벤트를 구독하여 shop review에 대해 redis로 캐싱하는 로직을 정의하는 component
@Component
class ShopReviewEventListener(
    private val shopReviewDynamoRepository: ShopReviewDynamoRepository,
    private val shopReviewRedisRepository: ShopReviewRedisRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    // review 생성 이벤트가 발행되면 처리하는 메소드
    @KafkaListener(
        topics = [KafkaTopics.shopReviewCreateTopic],
        groupId = KafkaConsumerGroups.createShopReviewGroup
    )
    fun cacheCreatedShopReview(shopReview: ShopReview) {
        shopReviewRedisRepository.cacheReview(shopReview)
            .subscribe()
    }

    // review 삭제 이벤트가 발행되면 처리하는 메소드
    @KafkaListener(
        topics = [KafkaTopics.shopReviewDeleteTopic],
        groupId = KafkaConsumerGroups.deleteShopReviewGroup
    )
    fun deleteCache(shopReview: ShopReview) {
        shopReviewRedisRepository.deleteReview(shopReview)
            .doOnError { logger.info("(Delete review cache) Cache not exist!") }
            .subscribe()
    }

    // shopReview의 개수 정합성을 따지기 위해 이벤트를 구독하는 메소드
    @KafkaListener(
        topics = [KafkaTopics.reviewCountValidateTopic],
        groupId = KafkaConsumerGroups.checkShopReviewCountGroup
    )
    fun checkShopReviewCount(countEvent: ShopReviewQuery.CountEvent) {
        /*
        1. review의 개수를 dynamo로부터 뽑아온다
        2. 둘을 비교한다 (reviewCountDto와 dynamo에서의 개수) -> 개수가 안 맞으면 dynamo로부터 풀 스캔해서 가져온다
         */
        when (countEvent.count) {
            0 -> with(countEvent) {
                shopReviewDynamoRepository.getAllReviewFlowByShopIdAndName(shopId, shopName).asFlux()
                    .flatMap { shopReviewRedisRepository.cacheReview(it) }
                    .subscribe()
            }
            else -> with(countEvent) {
                shopReviewDynamoRepository.getAllReviewFlowByShopIdAndName(shopId, shopName).asFlux()
                    .count()
                    .flatMapMany {
                        when (it == count.toLong()) {
                            true -> Flux.empty()
                            false -> with(countEvent) { cacheAllReviews(shopId, shopName) }
                        }
                    }
                    .subscribe()
            }
        }
    }

    /** 특정 shop에 대한 모든 review를 dynamo로부터 가져와서 모두 redis에 캐싱하는 메소드
     * @param shopId
     * @param shopName
     */
    private fun cacheAllReviews(shopId: String, shopName: String): Flux<Boolean> {
        return shopReviewDynamoRepository.getAllReviewFlowByShopIdAndName(shopId, shopName).asFlux()
            .flatMap { shopReviewRedisRepository.cacheReview(it) }
    }
}