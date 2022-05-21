package team.bakkas.domaindynamo.entity

import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbAutoGeneratedTimestampAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

// TODO LocalDateTime을 DynamoDB에서도 저장하는 방법?
@DynamoDbBean
data class Shop(
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("shop_id")
    var shopId: String = UUID.randomUUID().toString(),
    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("shop_name")
    var shopName: String = "",
    @get:DynamoDbAttribute("is_open")
    var isOpen: Boolean,
    @get:DynamoDbAttribute("open_time")
    var openTime: LocalDateTime,
    @get:DynamoDbAttribute("close_time")
    var closeTime: LocalDateTime,
    @get:DynamoDbAttribute("lot_number_address")
    var lotNumberAddress: String,
    @get:DynamoDbAttribute("road_name_address")
    var roadNameAddress: String,
    @get:DynamoDbAttribute("latitude")
    var latitude: Double,
    @get:DynamoDbAttribute("longitude")
    var longitude: Double,
    @get:DynamoDbAttribute("created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @get:DynamoDbAttribute("updated_at")
    var updatedAt: LocalDateTime?,
    @get:DynamoDbAttribute("average_score")
    var averageScore: Double,
    @get:DynamoDbAttribute("review_number")
    var reviewNumber: Int
): Serializable
