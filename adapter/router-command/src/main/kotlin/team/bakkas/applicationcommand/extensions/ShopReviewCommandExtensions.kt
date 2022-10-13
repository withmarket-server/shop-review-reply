package team.bakkas.applicationcommand.extensions

import team.bakkas.clientcommand.dto.ShopReviewCommand
import team.bakkas.dynamo.entity.ShopReview
import java.time.LocalDateTime
import java.util.UUID

// ReviewCreateDto를 Entity로 변환해주는 메소드
fun ShopReviewCommand.CreateRequest.toEntity() = ShopReview(
    reviewId = UUID.randomUUID().toString(),
    reviewTitle = this.reviewTitle,
    shopId = this.shopId,
    shopName = this.shopName,
    reviewContent = this.reviewContent,
    reviewScore = this.reviewScore,
    reviewPhotoList = this.reviewPhotoList,
    createdAt = LocalDateTime.now(),
    updatedAt = null
)