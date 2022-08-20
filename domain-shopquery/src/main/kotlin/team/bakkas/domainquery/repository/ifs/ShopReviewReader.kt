package team.bakkas.domainquery.repository.ifs

import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono
import team.bakkas.domaindynamo.entity.ShopReview

interface ShopReviewReader {

    // Cache hit 방식으로 ShopReview를 찾아오는 메소드
    fun findShopReviewByIdAndTitleWithCaching(reviewId: String, reviewTitle: String): Mono<ShopReview>

    // review list에 대한 flow를 반환해주는 메소드
    fun getShopReviewListFlowByShopIdAndNameWithCaching(shopId: String, shopName: String): Flow<ShopReview>
}