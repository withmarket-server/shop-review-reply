package team.bakkas.domainquery.reader

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import team.bakkas.common.utils.RedisUtils
import team.bakkas.domainquery.reader.ifs.ShopReviewReader
import team.bakkas.dynamo.shopReview.ShopReview
import team.bakkas.repository.ifs.dynamo.ShopReviewDynamoRepository
import team.bakkas.repository.ifs.redis.ShopReviewRedisRepository

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
     * @return Mono<ShopReview?>
     */
    override fun findReviewById(reviewId: String): Mono<ShopReview> {
        val key = RedisUtils.generateReviewRedisKey(reviewId)
        val alternativeMono = shopReviewDynamoRepository.findReviewById(reviewId)
            .single()
            .doOnSuccess { shopReviewRedisRepository.cacheReview(it).subscribe() }
            .onErrorResume { Mono.empty() }

        return shopReviewRedisRepository.findReviewById(key)
            .switchIfEmpty(alternativeMono)
    }

    /** review list에 대한 flow를 반환해주는 메소드
     * @param shopId
     * @param shopName
     * @return flow consisted with review of given shop
     */
    override fun getReviewsByShopId(shopId: String): Flow<ShopReview> =
        shopReviewRedisRepository.getShopReviewsByShopId(shopId)

    /** review list에 대한 flow를 반환해주는 메소드. Dynamo에서 먼저 가져와서 그걸로 review를 가져온다
     * @param shopId
     * @return flow consisted with review of given shop
     */
    override fun getReviewsOfShopWithCaching(shopId: String): Flow<ShopReview> {
        return shopReviewDynamoRepository.getAllReviewsByShopId(shopId)
            .map { findReviewById(it.reviewId).awaitSingle() }
    }
}