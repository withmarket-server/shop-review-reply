package team.bakkas.applicationkafka.extensions

import org.springframework.data.elasticsearch.core.geo.GeoPoint
import team.bakkas.dynamo.shop.Shop
import team.bakkas.elasticsearch.entity.SearchShop
import team.bakkas.elasticsearch.entity.vo.SearchDeliveryTipPerDistance

// Record System(DynamoDB)에 저장된 데이터를 ES에 맞게 가공하는 로직
fun Shop.toSearchEntity(): SearchShop = SearchShop(
    shopId = shopId,
    shopName = shopName,
    status = salesInfo.status,
    location = GeoPoint(latLon.latitude, latLon.longitude),
    deliveryTipPerDistanceList = deliveryTipPerDistanceList.map { SearchDeliveryTipPerDistance(it.distance, it.price) },
    category = categoryInfo.shopCategory,
    detailCategory = categoryInfo.shopDetailCategory,
    totalScore = totalScore,
    reviewNumber = reviewNumber,
    businessNumber = businessNumber
).apply {
    averageScore = when (reviewNumber) {
        0 -> 0.0
        else -> totalScore / reviewNumber
    }
}