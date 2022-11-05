package team.bakkas.domainquery.reader.ifs

import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono
import team.bakkas.dynamo.shopReview.ShopReview

interface ShopReviewReader {

    // Cache hit 방식으로 ShopReview를 찾아오는 메소드
    fun findReviewById(reviewId: String): Mono<ShopReview>

    // redis에 있는 review의 목록을 반환해주는 메소드
    fun getReviewsByShopId(shopId: String): Flow<ShopReview>

    // cache hit 방식으로 모든 review를 가져오는 메소드
    fun getReviewsOfShopWithCaching(shopId: String): Flow<ShopReview>
}