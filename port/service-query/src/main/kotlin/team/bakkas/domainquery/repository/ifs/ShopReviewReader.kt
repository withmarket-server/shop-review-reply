package team.bakkas.domainquery.repository.ifs

import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono
import team.bakkas.dynamo.entity.ShopReview

interface ShopReviewReader {

    // Cache hit 방식으로 ShopReview를 찾아오는 메소드
    fun findReviewByIdAndTitle(reviewId: String, reviewTitle: String): Mono<ShopReview>

    // redis에 있는 review의 목록을 반환해주는 메소드
    fun getReviewsByShopKey(shopId: String, shopName: String): Flow<ShopReview>

    // cache hit 방식으로 모든 review를 가져오는 메소드
    fun getReviewsOfShopWithCaching(shopId: String, shopName: String): Flow<ShopReview>
}