package team.bakkas.domainquery.reader.ifs

import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono
import team.bakkas.dynamo.shopReview.ShopReview

/**
 * ShopReviewReader
 * ShopReview에 대해서 Query Bysiness logic을 처리하는 interface
 * ShopReview의 Query 요청에 대해서 Facade pattern을 구현하는 interface이다.
 */
interface ShopReviewReader {

    fun findReviewById(reviewId: String): Mono<ShopReview>

    fun getReviewsByShopId(shopId: String): Flow<ShopReview>

    fun getReviewsOfShopWithCaching(shopId: String): Flow<ShopReview>
}