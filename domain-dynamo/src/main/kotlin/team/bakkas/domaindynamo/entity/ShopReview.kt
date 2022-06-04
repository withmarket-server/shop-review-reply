package team.bakkas.domaindynamo.entity

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

/** 가게에 대한 리뷰를 담당하는 엔티티 클래스. Review : Shop = N : 1 관계를 가진다.
 * Key : reviewId(Partition Key), reviewTitle(Sort Key)
 * GSI : Shop의 Partition Key, Sort Key
 * @author Brian
 * @since 22/06/02
 */
@DynamoDbBean
data class ShopReview(
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("review_id")
    var reviewId: String = UUID.randomUUID().toString(),
    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("review_title")
    var reviewTitle: String,
    @get:DynamoDbSecondaryPartitionKey(indexNames = ["shop_id-shop_name-index"])
    @get:DynamoDbAttribute("shop_id")
    var shopId: String,
    @get:DynamoDbSecondarySortKey(indexNames = ["shop_id-shop_name-index"])
    @get:DynamoDbAttribute("shop_name")
    var shopName: String,
    @get:DynamoDbAttribute("review_content")
    var reviewContent: String,
    @get:DynamoDbAttribute("review_score")
    var reviewScore: Double,
    @get:DynamoDbAttribute("review_photo_list")
    var reviewPhotoList: List<String>,
    @get:DynamoDbAttribute("created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @get:DynamoDbAttribute("updated_at")
    var updatedAt: LocalDateTime?
): Serializable
