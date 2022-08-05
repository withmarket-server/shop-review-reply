package team.bakkas.domainqueryservice.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domaindynamo.repository.dynamo.ShopReviewDynamoRepository
import java.time.Duration

/** shopReview에 대해서 Cache hit을 통한 Repository를 구현한 클래스
 * @param shopReviewDynamoRepository dynamoDB에 접근하여 데이터를 제어하는 레포지토리
 * @param shopReviewReactiveRedisTemplate Cache hit을 구현하기 위해서 Redis에 접근하는 template
 */
@Repository
class ShopReviewRepository(
    private val shopReviewDynamoRepository: ShopReviewDynamoRepository,
    private val shopReviewReactiveRedisTemplate: ReactiveRedisTemplate<String, ShopReview>
) {

    companion object {
        val DAYS_TO_LIVE = 1L

        fun generateRedisKey(reviewId: String, reviewTitle: String) = "shopReview-$reviewId-$reviewTitle"
    }

    /** Cache hit 방식으로 ShopReview를 찾아오는 메소드
     * @param reviewId
     * @param reviewTitle
     * @return Mono<ShopReview?>
     */
    fun findShopReviewByIdAndTitleWithCaching(reviewId: String, reviewTitle: String): Mono<ShopReview?> {
        val key = generateRedisKey(reviewId, reviewTitle)
        val alternativeMono = shopReviewDynamoRepository.findReviewByIdAndTitleAsync(reviewId, reviewTitle)
            .doOnSuccess {
                it?.let {
                    shopReviewReactiveRedisTemplate.opsForValue().set(key, it, Duration.ofDays(DAYS_TO_LIVE))
                        .subscribe()
                }
            }.onErrorResume {
                Mono.empty()
            }

        return shopReviewReactiveRedisTemplate.opsForValue().get(key)
            .switchIfEmpty(alternativeMono)
    }

    /** review list에 대한 flow를 반환해주는 메소드
     * @param shopId
     * @param shopName
     * @return Flow<Mono<ShopReview>> flow consisted with monos of shopReview
     */
    fun getShopReviewListFlowByShopIdAndNameWithCaching(shopId: String, shopName: String): Flow<Mono<ShopReview?>> {
        val reviewKeysFlow = shopReviewDynamoRepository.getAllReviewKeyFlowByShopIdAndName(shopId, shopName)
        return reviewKeysFlow.map {
            findShopReviewByIdAndTitleWithCaching(it.first, it.second)
        }
    }
}