package team.bakkas.servicecommand.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
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
    override fun deleteReview(reviewId: String, reviewTitle: String): Mono<ShopReview> {
        // review redis key
        val redisKey = RedisUtils.generateReviewRedisKey(reviewId, reviewTitle)
        val redisMono = shopReviewRedisRepository.findReviewByKey(redisKey)
            .switchIfEmpty(Mono.empty())
            .flatMap { shopReviewRedisRepository.deleteReview(it) }

        // 검증이 끝나면 review 삭제
        return shopReviewDynamoRepository.deleteReviewAsync(reviewId, reviewTitle) // 우선 dynamo에서 review를 제거하고
            .doOnSuccess { redisMono.subscribe() } // redis에서 캐시를 evict 처리한다
    }

    /** shop의 모든 review를 제거하는 service 메소드
     * @param shopId shop의 id
     * @param shopName shop의 name
     * @return Flux of shopReview
     */
    @Transactional
    override fun deleteAllReviewsOfShop(shopId: String, shopName: String): Flux<ShopReview> {
        return shopReviewDynamoRepository.getAllReviewFlowByShopIdAndName(shopId, shopName)
            .asFlux()
            .flatMap { shopReviewDynamoRepository.deleteReviewAsync(it.reviewId, it.reviewTitle) }
            .flatMap { shopReviewRedisRepository.deleteReview(it) }
    }
}