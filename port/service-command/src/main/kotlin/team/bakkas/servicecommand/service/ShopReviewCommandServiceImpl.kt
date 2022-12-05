package team.bakkas.servicecommand.service

import kotlinx.coroutines.reactor.asFlux
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import team.bakkas.common.utils.RedisUtils
import team.bakkas.servicecommand.service.ifs.ShopReviewCommandService
import team.bakkas.dynamo.shopReview.ShopReview
import team.bakkas.dynamo.shopReview.extensions.applyReplyCreated
import team.bakkas.dynamo.shopReview.extensions.applyReplyDeleted
import team.bakkas.repository.ifs.dynamo.ShopReviewDynamoRepository
import team.bakkas.repository.ifs.redis.ShopReviewRedisRepository

@Service
class ShopReviewCommandServiceImpl(
    private val shopReviewDynamoRepository: ShopReviewDynamoRepository,
    private val shopReviewRedisRepository: ShopReviewRedisRepository
) : ShopReviewCommandService {

    override fun createReview(shopReview: ShopReview): Mono<ShopReview> {
        // 검증이 끝나면 review 생성
        return shopReviewDynamoRepository.createReview(shopReview) // review를 dynamo에 저장하고
            .doOnSuccess { shopReviewRedisRepository.cacheReview(it).subscribe() } // 동시에 redis에 캐싱한다
    }

    override fun deleteReview(reviewId: String): Mono<ShopReview> {
        // review redis key
        val redisKey = RedisUtils.generateReviewRedisKey(reviewId)
        val redisMono = shopReviewRedisRepository.findReviewById(redisKey)
            .switchIfEmpty(Mono.empty())
            .flatMap { shopReviewRedisRepository.deleteReview(it.reviewId) }

        // 검증이 끝나면 review 삭제
        return shopReviewDynamoRepository.deleteReview(reviewId) // 우선 dynamo에서 review를 제거하고
            .doOnSuccess { redisMono.subscribe() } // redis에서 캐시를 evict 처리한다
    }

    /** shop의 모든 review를 제거하는 service 메소드
     * @param shopId shop의 id
     * @param shopName shop의 name
     * @return Flux of shopReview
     */
    override fun deleteAllReviewsOfShop(shopId: String): Flux<ShopReview> {
        return shopReviewDynamoRepository.getAllReviewsByShopId(shopId)
            .asFlux()
            .flatMap { shopReviewDynamoRepository.deleteReview(it.reviewId) }
            .doOnNext { shopReviewRedisRepository.deleteReview(it.reviewId).subscribe() }
    }

    // review를 soft delete하는 메소드
    override fun softDeleteReview(reviewId: String): Mono<ShopReview> {

        return shopReviewDynamoRepository.softDeleteReview(reviewId)
            .doOnSuccess { shopReviewRedisRepository.deleteReview(reviewId).subscribe() }
    }

    // shop의 모든 review를 soft delete하는 메소드
    override fun softDeleteAllReviewsOfShop(shopId: String): Flux<ShopReview> {
        return shopReviewDynamoRepository.getAllReviewsByShopId(shopId) // shopId, shopName에 대응하는 shop의 모든 review를 가져와서
            .asFlux()
            .flatMap { shopReviewDynamoRepository.softDeleteReview(it.reviewId) } // Dynamo에 있는 데이터는 모두 soft delete 처리하고
            .doOnNext { shopReviewRedisRepository.deleteReview(it.reviewId).subscribe() } // Redis에서는 지워버린다
    }

    override fun applyReplyCreated(reviewId: String): Mono<ShopReview> {
        return shopReviewDynamoRepository.findReviewById(reviewId) // record system으로부터 데이터를 찾아와서
            .map { it.applyReplyCreated() } // 답글 추가를 review에 반영하고
            .flatMap { shopReviewDynamoRepository.createReview(it) } // 다시 저장하고
            .doOnSuccess { shopReviewRedisRepository.cacheReview(it).subscribe() } // 이를 redis에도 반영한다
    }

    override fun applyReplyDeleted(reviewId: String): Mono<ShopReview> {
        return shopReviewDynamoRepository.findReviewById(reviewId) // record system으로부터 데이터를 찾아와서
            .map { it.applyReplyDeleted() } // 답글 삭제를 review에 반영하고
            .flatMap { shopReviewDynamoRepository.createReview(it) } // 다시 record system에 저장하고
            .doOnSuccess { shopReviewRedisRepository.cacheReview(it).subscribe() } // 성공하면 같은 내용을 redis에 저장한다
    }
}