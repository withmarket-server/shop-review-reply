package team.bakkas.applicationkafka.eventHandlers

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import team.bakkas.clientcommand.shopReview.ShopReviewCommand
import team.bakkas.dynamo.shopReview.ShopReview
import team.bakkas.elasticsearch.entity.SearchShop
import team.bakkas.elasticsearch.repository.ShopSearchRepository
import team.bakkas.elasticsearch.domainExtensions.applyCreateReview
import team.bakkas.elasticsearch.domainExtensions.applyDeleteReview
import team.bakkas.eventinterface.kafka.KafkaConsumerGroups
import team.bakkas.eventinterface.kafka.KafkaTopics
import team.bakkas.servicecommand.service.ifs.ShopCommandService
import team.bakkas.servicecommand.service.ifs.ShopReviewCommandService

/**
 * ShopReviewEventHandler
 * ShopReview에 대해서 발행된 이벤트를 처리하는 event handler
 * @param shopCommandService shop command logic을 처리하는 useCase layer class
 * @param shopReviewCommandService shopReview command logic을 처리하는 useCase layer class
 * @param shopSearchRepository es에 shop을 persist하는 repository
 */
@Component
class ShopReviewEventHandler(
    private val shopCommandService: ShopCommandService,
    private val shopReviewCommandService: ShopReviewCommandService,
    private val shopSearchRepository: ShopSearchRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = [KafkaTopics.shopReviewCreateTopic],
        groupId = KafkaConsumerGroups.shopReviewGroup
    )
    fun createShopReview(shopReview: ShopReview) {
        shopReviewCommandService.createReview(shopReview)
            .doOnNext { shopCommandService.applyCreateReview(it.shopId, it.reviewScore).subscribe() } // review 생성 완료시 shop에도 해당 사실 반영
            .doOnNext { applyCreateReviewToES(it.shopId, it.reviewScore).subscribe() } // ES에 저장된 shop에도 해당 정보 반영
            .subscribe()
    }

    @KafkaListener(
        topics = [KafkaTopics.shopReviewDeleteTopic],
        groupId = KafkaConsumerGroups.shopReviewGroup
    )
    fun deleteShopReview(deletedEvent: ShopReviewCommand.DeletedEvent) = with(deletedEvent) {
        shopReviewCommandService.softDeleteReview(reviewId) // review를 soft delete
            .doOnNext { shopCommandService.applyDeleteReview(it.shopId, it.reviewScore).subscribe() } // shopReview 삭제를 shop에도 반영
            .doOnNext { applyDeleteReviewToES(it.shopId, it.reviewScore).subscribe() } // ES에도 해당 변경 정보를 반영
            .subscribe()
    }

    private fun applyCreateReviewToES(shopId: String, reviewScore: Double): Mono<SearchShop> {
        return shopSearchRepository.findById(shopId)
            .map { it.applyCreateReview(reviewScore) }
            .flatMap { shopSearchRepository.save(it) }
    }

    private fun applyDeleteReviewToES(shopId: String, reviewScore: Double): Mono<SearchShop> {
        return shopSearchRepository.findById(shopId)
            .map { it.applyDeleteReview(reviewScore) }
            .flatMap { shopSearchRepository.save(it) }
    }
}