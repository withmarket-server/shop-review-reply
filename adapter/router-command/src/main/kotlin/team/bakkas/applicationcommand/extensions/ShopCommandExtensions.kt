package team.bakkas.applicationcommand.extensions

import team.bakkas.clientcommand.dto.ShopCommand
import team.bakkas.domaindynamo.entity.Shop
import java.time.LocalDateTime
import java.util.*

fun ShopCommand.CreateRequest.toEntity() = Shop(
    shopId = UUID.randomUUID().toString(),
    shopName = this.shopName,
    openTime = this.openTime,
    closeTime = this.closeTime,
    lotNumberAddress = this.lotNumberAddress,
    roadNameAddress = this.roadNameAddress,
    latitude = this.latitude,
    longitude = this.longitude,
    shopDescription = this.shopDescription,
    isBranch = this.isBranch,
    branchName = this.branchName,
    shopCategory = this.shopCategory,
    shopDetailCategory = this.shopDetailCategory,
    mainImage = this.mainImageUrl,
    representativeImageList = this.representativeImageUrlList,
    createdAt = LocalDateTime.now(),
    averageScore = 0.0,
    isOpen = false,
    reviewNumber = 0,
    updatedAt = null
)