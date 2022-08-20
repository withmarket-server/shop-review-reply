package team.bakkas.applicationkafka.eventListeners

import kotlinx.coroutines.flow.count
import kotlinx.coroutines.reactor.asFlux
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import team.bakkas.domainkafka.kafka.KafkaConsumerGroups
import team.bakkas.domainkafka.kafka.KafkaTopics
import team.bakkas.clientcommand.dto.ShopCommand
import team.bakkas.clientquery.dto.ShopQuery
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
    @KafkaListener(
        topics = [KafkaTopics.shopCreateTopic],
        groupId = KafkaConsumerGroups.createShopGroup
    )
    fun cacheCreatedShop(shop: Shop) {
        shopRedisRepository.cacheShop(shop).subscribe()
    }

    /** Shop의 개수가 정합을 이루고 있는지 검사하는 리스너 메소드
     * @param shopCountDto redis로부터 count를 전달받는 파라미터
     */
    @KafkaListener(
        topics = [KafkaTopics.shopCountTopic],
        groupId = KafkaConsumerGroups.checkShopCountGroup
    )
    fun checkShopCount(shopCountDto: ShopQuery.ShopCountDto) {
        /*
        1. shop의 개수를 dynamo로부터 뽑아온다
        2. 둘을 비교한다 (shopCountDto와 dynamo에서의 개수) -> 개수가 안 맞으면 dynamo로부터 풀 스캔해서 가져온다
         */
        when (shopCountDto.shopCount) {
            // redis에 shop이 하나도 존재하지 않는 경우 dynamo로부터 모든 아이템을 가져와서 캐싱한다
            0 -> shopDynamoRepository.getAllShops().asFlux()
                .flatMap { shopRedisRepository.cacheShop(it) }
                .subscribe()
            else -> shopDynamoRepository.getAllShops().asFlux().count()
                .flatMapMany {
                    when (it == shopCountDto.shopCount.toLong()) {
                        true -> Flux.empty() // 개수가 일치하면 아무 동작도 시행하지 않는다
                        false -> cacheAllShops() // 개수가 불일치하면 모든 shop을 dynamo로부터 가져와서 캐싱한다
                    }
                }
                .subscribe()
        }
    }

    // shop에 대해서 리뷰가 작성되면 카운트를 증가시켜주는 리스너 메소드
    @KafkaListener(
        topics = [KafkaTopics.reviewCountEventTopic],
        groupId = KafkaConsumerGroups.updateShopReviewCountGroup
    )
    fun updateReviewCount(reviewCountEventDto: ShopCommand.ReviewCountEventDto) {
        /*
        1. Shop을 DynamoDB로부터 가져온다
        2. DynamoDB로부터 가져온 Shop에 대해서 averageScore, reviewCount를 조작한다.
        3. 해당 Shop을 DynamoDB에 갱신하고, 동시에 Redis에도 갱신한다.
         */
        val shopMono = with(reviewCountEventDto) {
            shopDynamoRepository.findShopByIdAndNameAsync(shopId, shopName)
        }.map { it!! }
            .map { changeShopInfo(it, reviewCountEventDto) }

        // 비동기적으로 dynamo, redis에 해당 정보 저장
        shopMono.flatMap { shopDynamoRepository.createShopAsync(it) }.subscribe()
        shopMono.flatMap { shopRedisRepository.cacheShop(it) }.subscribe()
    }

    // shop의 변화를 반영해주는 메소드
    private fun changeShopInfo(shop: Shop, reviewCountEventDto: ShopCommand.ReviewCountEventDto): Shop {
        return when (reviewCountEventDto.isGenerated) {
            true -> applyGenerateReview(shop, reviewCountEventDto.reviewScore)
            false -> applyDeleteReview(shop, reviewCountEventDto.reviewScore)
        }
    }

    // review가 삭제되었을 때 해당 리뷰 삭제를 shop에 반영해주는 메소드
    private fun applyDeleteReview(shop: Shop, reviewScore: Double): Shop = with(shop) {
        val newTotalScore = averageScore * reviewNumber - reviewScore // 새로 반영될 총점 계산

        // averageScore을 수정한다.
        averageScore = when (reviewNumber) {
            1 -> 0.0
            else -> newTotalScore / (reviewNumber - 1)
        }

        reviewNumber -= 1

        return@with this
    }

    // review가 생성되었을 때 해당 리뷰 생성을 shop에 반영해주는 메소드
    private fun applyGenerateReview(shop: Shop, reviewScore: Double): Shop = with(shop) {
        val newTotalScore = averageScore * reviewNumber + reviewScore
        averageScore = newTotalScore / (reviewNumber + 1)
        reviewNumber += 1

        return@with this
    }

    // redis의 count와 dynamo의 count를 비교한 후에 캐싱하는 메소드
    private fun cacheAllShops(): Flux<Boolean> {
        return shopDynamoRepository.getAllShops().asFlux()
            .flatMap { shopRedisRepository.cacheShop(it) }
    }
}