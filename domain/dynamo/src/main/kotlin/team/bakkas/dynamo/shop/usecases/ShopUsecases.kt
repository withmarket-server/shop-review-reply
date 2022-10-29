package team.bakkas.dynamo.shop.usecases

import team.bakkas.dynamo.shop.Shop
import java.time.LocalDateTime

// Shop에 대한 유스케이스를 정의하는 코틀린 파일

// 리뷰 생성을 shop에 반영해주는 메소드
fun Shop.applyReviewCreate(reviewScore: Double): Shop {
    this.reviewNumber += 1 // 리뷰의 개수를 증가시킨다
    this.totalScore += reviewScore

    return this
}

// 리뷰 삭제를 shop에 반영해주는 메소드
fun Shop.applyReviewDelete(reviewScore: Double): Shop {
    this.reviewNumber -= 1
    this.totalScore -= reviewScore

    return this
}

// Shop을 soft delete 처리를 해주는 메소드
fun Shop.softDelete(): Shop {
    this.deletedAt = LocalDateTime.now()

    return this
}