package team.bakkas.applicationcommand.extensions

import team.bakkas.clientcommand.dto.ShopCommand
import team.bakkas.dynamo.shop.Shop
import team.bakkas.dynamo.shop.vo.*
import java.time.LocalDateTime
import java.util.*

fun ShopCommand.CreateRequest.toEntity() = Shop(
    shopId = UUID.randomUUID().toString(),
    shopName = shopName,
    salesInfo = SalesInfo(isOpen = false, openTime = openTime, closeTime = closeTime, restDayList = restDayList),
    addressInfo = AddressInfo(lotNumberAddress, roadNameAddress, detailAddress),
    latLon = LatLon(latitude, longitude),
    shopImageInfo = ShopImageInfo(mainImageUrl, representativeImageUrlList),
    branchInfo = BranchInfo(isBranch, branchName),
    categoryInfo = CategoryInfo(shopCategory, shopDetailCategory),
    deliveryTipPerDistanceList = deliveryTipPerDistanceList,
    totalScore = 0.0,
    reviewNumber = 0,
    shopDescription = shopDescription
)