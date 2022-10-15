package team.bakkas.applicationkafka.eventListeners

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import team.bakkas.clientcommand.shop.ShopCommand
import team.bakkas.dynamo.shop.Shop
import team.bakkas.eventinterface.kafka.KafkaConsumerGroups
import team.bakkas.eventinterface.kafka.KafkaTopics
import team.bakkas.repository.ifs.dynamo.ShopDynamoRepository
import team.bakkas.repository.ifs.redis.ShopRedisRepository

// Kafka에 발행된 shop관련 메시지를 redis에 캐싱하는 리스너를 정의하는 클래스
@Component
class ShopEventListener(
    private val shopDynamoRepository: ShopDynamoRepository,
    private val shopRedisRepository: ShopRedisRepository
) {

    // withmarket.shop.create 토픽에 있는 메시지를 읽어내서 redis에 캐싱하는 메소드
    @KafkaListener(
        topics = [KafkaTopics.shopCreateTopic],
        groupId = KafkaConsumerGroups.createShopGroup
    )
    fun cacheCreatedShop(shop: Shop) {
        shopRedisRepository.cacheShop(shop).subscribe()
    }

    // shop에 대해서 리뷰가 작성되면 카운트를 증가시켜주는 리스너 메소드
    @KafkaListener(
        topics = [KafkaTopics.reviewGenerateEventTopic],
        groupId = KafkaConsumerGroups.updateShopReviewCountGroup
    )
    fun updateReviewCount(reviewCreatedEvent: ShopCommand.ReviewCreatedEvent) {
        /*
        1. Shop을 DynamoDB로부터 가져온다
        2. DynamoDB로부터 가져온 Shop에 대해서 averageScore, reviewCount를 조작한다.
        3. 해당 Shop을 DynamoDB에 갱신하고, 동시에 Redis에도 갱신한다.
         */
        val shopMono = with(reviewCreatedEvent) {
            shopDynamoRepository.findShopByIdAndName(shopId, shopName)
        }.map { it!! }
            .map { changeShopInfo(it, reviewCreatedEvent) }

        // 비동기적으로 dynamo, redis에 해당 정보 저장
        shopMono.flatMap { shopDynamoRepository.createShop(it) }.subscribe()
        shopMono.flatMap { shopRedisRepository.cacheShop(it) }.subscribe()
    }

    // shop의 변화를 반영해주는 메소드
    private fun changeShopInfo(shop: Shop, reviewCreatedEvent: ShopCommand.ReviewCreatedEvent): Shop {
        return when (reviewCreatedEvent.isGenerated) {
            true -> applyGenerateReview(shop, reviewCreatedEvent.reviewScore)
            false -> applyDeleteReview(shop, reviewCreatedEvent.reviewScore)
        }
    }

    // review가 삭제되었을 때 해당 리뷰 삭제를 shop에 반영해주는 메소드
    private fun applyDeleteReview(shop: Shop, reviewScore: Double): Shop = with(shop) {
        // averageScore을 수정한다.
        totalScore = when (reviewNumber) {
            1 -> 0.0
            else -> totalScore - reviewScore
        }

        reviewNumber -= 1

        return@with this
    }

    // review가 생성되었을 때 해당 리뷰 생성을 shop에 반영해주는 메소드
    private fun applyGenerateReview(shop: Shop, reviewScore: Double): Shop = with(shop) {
        totalScore += reviewScore
        reviewNumber += 1

        return@with this
    }
}