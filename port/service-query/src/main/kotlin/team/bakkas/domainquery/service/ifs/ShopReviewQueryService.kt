package team.bakkas.domainquery.service.ifs

import team.bakkas.dynamo.shopReview.ShopReview

interface ShopReviewQueryService {

    suspend fun findReviewByIdAndTitle(reviewId: String, reviewTitle: String): ShopReview?

    suspend fun getReviewsByShopId(shopId: String): List<ShopReview>
}