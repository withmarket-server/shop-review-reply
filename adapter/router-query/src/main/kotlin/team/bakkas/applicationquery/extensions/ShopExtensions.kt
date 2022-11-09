package team.bakkas.applicationquery.extensions

import team.bakkas.clientquery.shop.ShopQuery
import team.bakkas.dynamo.shop.Shop

fun Shop.toSimpleResponse() = ShopQuery.SimpleResponse(
    shopId = this.shopId,
    shopName = this.shopName,
    status = this.salesInfo.status,
    lotNumberAddress = this.addressInfo.lotNumberAddress,
    roadNameAddress = this.addressInfo.roadNameAddress,
    latitude = this.latLon.latitude,
    longitude = this.latLon.longitude,
    averageScore = 0.0,
    reviewNumber = this.reviewNumber,
    mainImage = this.shopImageInfo.mainImage,
    shopDescription = this.shopDescription,
    shopCategory = this.categoryInfo.shopCategory,
    shopDetailCategory = this.categoryInfo.shopDetailCategory,
    isBranch = this.branchInfo.isBranch,
    branchName = this.branchInfo.branchName
).apply { averageScore = when(reviewNumber) {
    0 -> 0.0
    else -> totalScore / reviewNumber
} }