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

/**
 * ShopReviewReaderImpl(shopReviewDynamoRepository: ShopReviewDynamoRepository, shopReviewRedisRepository: ShopReviewRedisRepository)
 * ShopReviewReader의 구현체. Facade pattern을 구현한다.
 * @param shopReviewDynamoRepository
 * @param shopReviewRedisRepository
 */
@Repository
class ShopReviewReaderImpl(
    private val shopReviewDynamoRepository: ShopReviewDynamoRepository,
    private val shopReviewRedisRepository: ShopReviewRedisRepository
) : ShopReviewReader {

    override fun findReviewById(reviewId: String): Mono<ShopReview> {
        val key = RedisUtils.generateReviewRedisKey(reviewId)

        // redis에 review가 존재하지 않았을 때 dynamo에서 review를 가져온다
        val alternativeMono = shopReviewDynamoRepository.findReviewById(reviewId)
            .single()
            .doOnSuccess { shopReviewRedisRepository.cacheReview(it).subscribe() }
            .onErrorResume { Mono.empty() }

        return shopReviewRedisRepository.findReviewById(key)
            .switchIfEmpty(alternativeMono)
    }

    override fun getReviewsByShopId(shopId: String): Flow<ShopReview> =
        shopReviewRedisRepository.getShopReviewsByShopId(shopId)


    override fun getReviewsOfShopWithCaching(shopId: String): Flow<ShopReview> {
        return shopReviewDynamoRepository.getAllReviewsByShopId(shopId)
            .map { findReviewById(it.reviewId).awaitSingle() }
    }
}