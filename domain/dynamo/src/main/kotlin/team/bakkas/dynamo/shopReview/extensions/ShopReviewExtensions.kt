package team.bakkas.dynamo.shopReview.extensions

import team.bakkas.dynamo.shopReview.ShopReview
import java.time.LocalDateTime

// ShopReview에 대한 usecase를 정의하는 코틀린 파일

// Review를 soft delete하는 메소드
fun ShopReview.softDelete(): ShopReview {
    this.deletedAt = LocalDateTime.now()

    return this
}

// review에 대해서 reply가 작성되었을 때 isReplyExists를 true로 바꾼다
fun ShopReview.applyReplyCreated(): ShopReview {
    this.isReplyExists = true

    return this
}

// review에 대해서 reply가 삭제되었을 때 isReplyExists를 false로 바꾼다
fun ShopReview.applyReplyDeleted(): ShopReview {
    this.isReplyExists = false

    return this
}