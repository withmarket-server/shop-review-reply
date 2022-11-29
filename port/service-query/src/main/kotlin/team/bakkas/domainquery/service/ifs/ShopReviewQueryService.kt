package team.bakkas.domainquery.service.ifs

import team.bakkas.dynamo.shopReview.ShopReview

/**
 * ShopReviewQueryService
 * ShopReview에 대한 query business logic을 처리하는 service interface
 * Clean Architecture의 UseCase layer에 대응한다
 */
interface ShopReviewQueryService {

    suspend fun findReviewById(reviewId: String): ShopReview?

    suspend fun getReviewsByShopId(shopId: String): List<ShopReview>
}