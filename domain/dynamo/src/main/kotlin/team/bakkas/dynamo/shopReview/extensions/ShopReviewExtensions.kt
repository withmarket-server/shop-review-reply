package team.bakkas.dynamo.shopReview.extensions

import team.bakkas.dynamo.shopReview.ShopReview
import java.time.LocalDateTime

// ShopReview에 대한 usecase를 정의하는 코틀린 파일

// Review를 soft delete하는 메소드
fun ShopReview.softDelete(): ShopReview {
    this.deletedAt = LocalDateTime.now()

    return this
}