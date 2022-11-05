package team.bakkas.applicationquery.extensions

import team.bakkas.clientquery.shopReview.ShopReviewQuery
import team.bakkas.dynamo.shopReview.ShopReview

fun ShopReview.toSimpleResponse() = ShopReviewQuery.SimpleResponse(
    reviewId = this.reviewId,
    reviewTitle = this.reviewTitle,
    shopId = this.shopId,
    reviewContent = this.reviewContent,
    reviewScore = this.reviewScore,
    reviewPhotoList = this.reviewPhotoList,
    createdAt = this.createdAt
)