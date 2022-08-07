package team.bakkas.domainshopcommand.extensions

import team.bakkas.clientcommand.dto.ShopReviewCommand
import team.bakkas.domaindynamo.entity.ShopReview
import java.time.LocalDateTime

// ReviewCreateDto를 Entity로 변환해주는 메소드
fun ShopReviewCommand.CreateDto.toEntity() = ShopReview(
    reviewId = this.reviewId,
    reviewTitle = this.reviewTitle,
    shopId = this.shopId,
    shopName = this.shopName,
    reviewContent = this.reviewContent,
    reviewScore = this.reviewScore,
    reviewPhotoList = this.reviewPhotoList,
    createdAt = LocalDateTime.now(),
    updatedAt = null
)