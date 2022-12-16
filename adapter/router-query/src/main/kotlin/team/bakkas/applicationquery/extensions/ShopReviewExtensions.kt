package team.bakkas.applicationquery.extensions

import team.bakkas.clientquery.shopReview.ShopReviewQuery
import team.bakkas.dynamo.reply.Reply
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

// ShopReview를 reply가 딸려있는 response로 변환하는 메소드
// reply를 nullable로 받아서, reply가 null이 아닌 경우 reply response를 추가한다
fun ShopReview.toWithReplyResponse(reply: Reply?) = ShopReviewQuery.WithReplyResponse(
    reviewId = this.reviewId,
    reviewTitle = this.reviewTitle,
    shopId = this.shopId,
    reviewContent = this.reviewContent,
    reviewScore = this.reviewScore,
    reviewPhotoList = this.reviewPhotoList,
    createdAt = this.createdAt,
    reply = null
).apply {
    reply?.let { this.reply = reply.toSimpleResponse() }
}