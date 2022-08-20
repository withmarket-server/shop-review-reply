package team.bakkas.domainquery.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import team.bakkas.common.utils.RedisUtils
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domaindynamo.repository.dynamo.ShopReviewDynamoRepository
import team.bakkas.domaindynamo.repository.redis.ShopReviewRedisRepository
import team.bakkas.domainquery.repository.ifs.ShopReviewReader

/** shopReview에 대해서 Cache hit을 통한 Repository를 구현한 클래스
 * @param shopReviewDynamoRepository dynamoDB에 접근하여 데이터를 제어하는 레포지토리
 * @param shopReviewReactiveRedisTemplate Cache hit을 구현하기 위해서 Redis에 접근하는 template
 */
@Repository
class ShopReviewReaderImpl(
    private val shopReviewDynamoRepository: ShopReviewDynamoRepository,
    private val shopReviewRedisRepository: ShopReviewRedisRepository
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
            .doOnSuccess { shopReviewRedisRepository.cacheReview(it).subscribe() }
            .onErrorResume { Mono.empty() }

        return shopReviewRedisRepository.findReviewByKey(key)
            .switchIfEmpty(alternativeMono)
    }

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

    /** review list에 대한 flow를 반환해주는 메소드
     * @param shopId
     * @param shopName
     * @return flow consisted with review of given shop
     */
    override fun getReviewFlowByShopIdAndName(shopId: String, shopName: String): Flow<ShopReview> =
        shopReviewRedisRepository.getShopReviewFlowByShopIdAndName(shopId, shopName)
}