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