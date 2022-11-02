package team.bakkas.servicecommand.service

import kotlinx.coroutines.reactor.asFlux
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import team.bakkas.common.utils.RedisUtils
import team.bakkas.servicecommand.service.ifs.ShopReviewCommandService
import team.bakkas.dynamo.shopReview.ShopReview
import team.bakkas.repository.ifs.dynamo.ShopReviewDynamoRepository
import team.bakkas.repository.ifs.redis.ShopReviewRedisRepository

@Service
class ShopReviewCommandServiceImpl(
    private val shopReviewDynamoRepository: ShopReviewDynamoRepository,
    private val shopReviewRedisRepository: ShopReviewRedisRepository
) : ShopReviewCommandService {

    @Transactional
    override fun createReview(shopReview: ShopReview): Mono<ShopReview> {
        // 검증이 끝나면 review 생성
        return shopReviewDynamoRepository.createReviewAsync(shopReview) // review를 dynamo에 저장하고
            .doOnSuccess { shopReviewRedisRepository.cacheReview(it).subscribe() } // 동시에 redis에 캐싱한다
    }

    @Transactional
    override fun deleteReview(reviewId: String): Mono<ShopReview> {
        // review redis key
        val redisKey = RedisUtils.generateReviewRedisKey(reviewId)
        val redisMono = shopReviewRedisRepository.findReviewById(redisKey)
            .switchIfEmpty(Mono.empty())
            .flatMap { shopReviewRedisRepository.deleteReview(it) }

        // 검증이 끝나면 review 삭제
        return shopReviewDynamoRepository.deleteReviewAsync(reviewId) // 우선 dynamo에서 review를 제거하고
            .doOnSuccess { redisMono.subscribe() } // redis에서 캐시를 evict 처리한다
    }

    /** shop의 모든 review를 제거하는 service 메소드
     * @param shopId shop의 id
     * @param shopName shop의 name
     * @return Flux of shopReview
     */
    @Transactional
    override fun deleteAllReviewsOfShop(shopId: String): Flux<ShopReview> {
        return shopReviewDynamoRepository.getAllShopsByShopId(shopId)
            .asFlux()
            .flatMap { shopReviewDynamoRepository.deleteReviewAsync(it.reviewId) }
            .flatMap { shopReviewRedisRepository.deleteReview(it) }
    }

    // review를 soft delete하는 메소드
    @Transactional
    override fun softDeleteReview(reviewId: String): Mono<ShopReview> {

        return shopReviewDynamoRepository.softDeleteReview(reviewId)
            .doOnSuccess { shopReviewRedisRepository.softDeleteReview(reviewId).subscribe() }
    }

    // shop의 모든 review를 soft delete하는 메소드
    @Transactional
    override fun softDeleteAllReviewsOfShop(shopId: String): Flux<ShopReview> {
        return shopReviewDynamoRepository.getAllShopsByShopId(shopId) // shopId, shopName에 대응하는 shop의 모든 review를 가져와서
            .asFlux()
            .flatMap { shopReviewDynamoRepository.softDeleteReview(it.reviewId) } // Dynamo에 있는 데이터는 모두 soft delete 처리하고
            .doOnNext { shopReviewRedisRepository.deleteReview(it).subscribe() }
    }
}