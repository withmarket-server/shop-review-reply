package team.bakkas.applicationcommand.extensions

import team.bakkas.clientcommand.shopReview.ShopReviewCommand
import team.bakkas.dynamo.shopReview.ShopReview
import java.time.LocalDateTime
import java.util.UUID

// ReviewCreateDto를 Entity로 변환해주는 메소드
fun ShopReviewCommand.CreateRequest.toEntity() = ShopReview(
    reviewId = UUID.randomUUID().toString(),
    reviewTitle = this.reviewTitle,
    shopId = this.shopId,
    reviewContent = this.reviewContent,
    reviewScore = this.reviewScore,
    reviewPhotoList = this.reviewPhotoList
)