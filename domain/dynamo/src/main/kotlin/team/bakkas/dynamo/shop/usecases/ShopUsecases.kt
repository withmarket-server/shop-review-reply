package team.bakkas.dynamo.shop.usecases

import team.bakkas.dynamo.shop.Shop
import team.bakkas.dynamo.shop.vo.sale.Days
import java.time.LocalDateTime
import java.time.LocalTime

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

// 가게의 이름을 바꾸는 메소드
fun Shop.changeShopName(shopName: String?): Shop {
    shopName?.let { this.shopName = shopName }
    this.updatedAt = LocalDateTime.now()

    return this
}

// 가게의 이미지 정보를 변경하는 메소드
fun Shop.changeMainImage(mainImage: String?): Shop {
    mainImage?.let { this.shopImageInfo.mainImage = mainImage }
    this.updatedAt = LocalDateTime.now()

    return this
}

// 가게의 대표이미지 리스트를 변경하는 메소드
fun Shop.changeRepresentativeImageList(representativeImageList: List<String>?): Shop {
    representativeImageList?.let {
        this.shopImageInfo.representativeImageList = representativeImageList
    }
    this.updatedAt = LocalDateTime.now()

    return this
}

// 가게의 여닫는 시간을 변경하는 메소드
fun Shop.changeOpenCloseTime(openTime: LocalTime, closeTime: LocalTime): Shop {
    with(this.salesInfo) {
        this.openTime = openTime
        this.closeTime = closeTime
    }
    this.updatedAt = LocalDateTime.now()

    return this
}

// 휴무일을 변경하는 메소드
fun Shop.changeRestDayList(restDayList: List<Days>?): Shop {
    restDayList?.let { this.salesInfo.restDayList - restDayList }
    this.updatedAt = LocalDateTime.now()

    return this
}