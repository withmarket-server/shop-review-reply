package team.bakkas.applicationkafka.eventHandlers

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import team.bakkas.clientcommand.shopReview.ShopReviewCommand
import team.bakkas.dynamo.shopReview.ShopReview
import team.bakkas.elasticsearch.entity.SearchShop
import team.bakkas.elasticsearch.repository.ShopSearchRepository
import team.bakkas.elasticsearch.usecases.applyCreateReview
import team.bakkas.elasticsearch.usecases.applyDeleteReview
import team.bakkas.eventinterface.kafka.KafkaConsumerGroups
import team.bakkas.eventinterface.kafka.KafkaTopics
import team.bakkas.servicecommand.service.ifs.ShopCommandService
import team.bakkas.servicecommand.service.ifs.ShopReviewCommandService

// kafka로부터 이벤트를 구독하여 shop review에 대해 redis로 캐싱하는 로직을 정의하는 component
@Component
class ShopReviewEventHandler(
    private val shopCommandService: ShopCommandService,
    private val shopReviewCommandService: ShopReviewCommandService,
    private val shopSearchRepository: ShopSearchRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    // review 생성 이벤트가 발행되면 dynamo, redids에 반영하는 메소드
    @KafkaListener(
        topics = [KafkaTopics.shopReviewCreateTopic],
        groupId = KafkaConsumerGroups.shopReviewGroup
    )
    fun createShopReview(shopReview: ShopReview) {
        shopReviewCommandService.createReview(shopReview) // shopReview를 우선 dynamo에 넣고
            .doOnNext { shopCommandService.applyCreateReview(it.shopId, it.reviewScore).subscribe() }
            .doOnNext { applyCreateReviewToES(it.shopId, it.reviewScore).subscribe() }
            .subscribe()
    }

    // review 삭제 이벤트가 발행되면 dynamo, redis에 반영하는 메소드 (soft delete)
    @KafkaListener(
        topics = [KafkaTopics.shopReviewDeleteTopic],
        groupId = KafkaConsumerGroups.shopReviewGroup
    )
    fun deleteShopReview(deletedEvent: ShopReviewCommand.DeletedEvent) = with(deletedEvent) {
        shopReviewCommandService.softDeleteReview(reviewId) // 우선 review부터 삭제해주고
            .doOnNext { shopCommandService.applyDeleteReview(it.shopId, it.reviewScore).subscribe() }
            .doOnNext { applyDeleteReivewToES(it.shopId, it.reviewScore).subscribe() }
            .subscribe()
    }

    // Elasticsearch의 document에 review 작성을 반영하는 메소드
    private fun applyCreateReviewToES(shopId: String, reviewScore: Double): Mono<SearchShop> {
        return shopSearchRepository.findById(shopId)
            .map { it.applyCreateReview(reviewScore) }
            .flatMap { shopSearchRepository.save(it) }
    }

    // Elasticsearch의 document에 review 삭제를 반영하는 메소드
    private fun applyDeleteReivewToES(shopId: String, reviewScore: Double): Mono<SearchShop> {
        return shopSearchRepository.findById(shopId)
            .map { it.applyDeleteReview(reviewScore) }
            .flatMap { shopSearchRepository.save(it) }
    }
}