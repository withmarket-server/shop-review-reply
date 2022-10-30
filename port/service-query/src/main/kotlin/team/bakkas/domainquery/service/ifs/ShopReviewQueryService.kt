package team.bakkas.domainquery.service.ifs

import team.bakkas.dynamo.shopReview.ShopReview

interface ShopReviewQueryService {

    suspend fun findReviewById(reviewId: String): ShopReview?

    suspend fun getReviewsByShopId(shopId: String): List<ShopReview>
}