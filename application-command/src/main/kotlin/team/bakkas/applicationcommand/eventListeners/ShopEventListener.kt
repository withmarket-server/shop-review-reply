package team.bakkas.applicationcommand.eventListeners

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import team.bakkas.applicationcommand.kafka.KafkaConsumerGroups
import team.bakkas.applicationcommand.kafka.KafkaTopics
import team.bakkas.clientcommand.dto.ShopCommand
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domaindynamo.repository.dynamo.ShopDynamoRepository
import team.bakkas.domaindynamo.repository.redis.ShopRedisRepository

// TODO 별도의 어플리케이션으로 분리해야함
// Kafka에 발행된 shop관련 메시지를 redis에 캐싱하는 리스너를 정의하는 클래스
@Component
class ShopEventListener(
    private val shopDynamoRepository: ShopDynamoRepository,
    private val shopRedisRepository: ShopRedisRepository
) {

    // withmarket.shop.create 토픽에 있는 메시지를 읽어내서 redis에 캐싱하는 메소드
    @KafkaListener(topics = [KafkaTopics.shopCreateTopic], groupId = KafkaConsumerGroups.createShopGroup)
    fun cacheCreatedShop(shop: Shop) {
        shopRedisRepository.cacheShop(shop).subscribe()
    }

    // shop에 대해서 리뷰가 작성되면 카운트를 증가시켜주는 리스너 메소드
    @KafkaListener(
        topics = [KafkaTopics.reviewCountEventTopic],
        groupId = KafkaConsumerGroups.updateShopReviewCountGroup
    )
    fun updateReviewCount(reviewCountEventDto: ShopCommand.ReviewCountEventDto) {

    }
}