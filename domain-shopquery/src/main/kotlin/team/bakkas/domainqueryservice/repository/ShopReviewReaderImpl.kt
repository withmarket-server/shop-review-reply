package team.bakkas.domainqueryservice.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import team.bakkas.common.utils.RedisUtils
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domaindynamo.repository.dynamo.ShopReviewDynamoRepository
import team.bakkas.domainqueryservice.repository.ifs.ShopReviewReader
import java.time.Duration

/** shopReview에 대해서 Cache hit을 통한 Repository를 구현한 클래스
 * @param shopReviewDynamoRepository dynamoDB에 접근하여 데이터를 제어하는 레포지토리
 * @param shopReviewReactiveRedisTemplate Cache hit을 구현하기 위해서 Redis에 접근하는 template
 */
@Repository
class ShopReviewReaderImpl(
    private val shopReviewDynamoRepository: ShopReviewDynamoRepository,
    private val shopReviewReactiveRedisTemplate: ReactiveRedisTemplate<String, ShopReview>
) : ShopReviewReader {

    /** Cache hit 방식으로 ShopReview를 찾아오는 메소드
     * @param reviewId
     * @param reviewTitle
     * @return Mono<ShopReview?>
     */
    override fun findShopReviewByIdAndTitleWithCaching(reviewId: String, reviewTitle: String): Mono<ShopReview> {
        val key = RedisUtils.generateReviewRedisKey(reviewId, reviewTitle)
        val alternativeMono = shopReviewDynamoRepository.findReviewByIdAndTitleAsync(reviewId, reviewTitle)
            .single()
            .doOnSuccess {
                shopReviewReactiveRedisTemplate.opsForValue().set(key, it, Duration.ofDays(RedisUtils.DAYS_TO_LIVE))
                    .subscribe()
            }
            .onErrorResume { Mono.empty() }

        return shopReviewReactiveRedisTemplate.opsForValue().get(key)
            .switchIfEmpty(alternativeMono)
    }

    // TODO 모든 review를 올리는 메소드는 따로 분리하고 cache-hit 시키고, 나머지는 모두 cache-hit으로는 하지말자.

    /** review list에 대한 flow를 반환해주는 메소드
     * @param shopId
     * @param shopName
     * @return Flow<Mono<ShopReview>> flow consisted with monos of shopReview
     */
    override fun getShopReviewListFlowByShopIdAndNameWithCaching(
        shopId: String,
        shopName: String
    ): Flow<ShopReview> {
        val reviewKeysFlow = shopReviewDynamoRepository.getAllReviewKeyFlowByShopIdAndName(shopId, shopName)
        return reviewKeysFlow.map { findShopReviewByIdAndTitleWithCaching(it.first, it.second).awaitSingle() }
    }
}