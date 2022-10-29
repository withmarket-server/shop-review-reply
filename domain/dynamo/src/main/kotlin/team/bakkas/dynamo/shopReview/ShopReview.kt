package team.bakkas.dynamo.shopReview

import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTag
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*
import team.bakkas.dynamo.BaseTimeEntity
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
class ShopReview(
    var reviewId: String = UUID.randomUUID().toString(),
    var reviewTitle: String = "",
    var shopId: String = "",
    var reviewContent: String = "",
    var reviewScore: Double = 0.0,
    var reviewPhotoList: List<String> = listOf()
) : Serializable, BaseTimeEntity() {

    companion object {
        val shopSecondaryIndexName = "shop_id-shop_name-index"

        val tableSchema = TableSchema.builder(ShopReview::class.java)
            .newItemSupplier(::ShopReview)
            .addAttribute(String::class.java) {
                it.name("review_id").getter(ShopReview::reviewId::get)
                    .setter(ShopReview::reviewId::set)
                    .tags(StaticAttributeTags.primaryPartitionKey())
            }
            .addAttribute(String::class.java) {
                it.name("review_title").getter(ShopReview::reviewTitle::get)
                    .setter(ShopReview::reviewTitle::set)
                    .tags(StaticAttributeTags.primarySortKey())
            }
            .addAttribute(String::class.java) {
                it.name("shop_id").getter(ShopReview::shopId::get)
                    .setter(ShopReview::shopId::set)
                    .tags(StaticAttributeTags.secondaryPartitionKey(shopSecondaryIndexName))
            }
            .addAttribute(String::class.java) {
                it.name("review_content").getter(ShopReview::reviewContent::get)
                    .setter(ShopReview::reviewContent::set)
            }
            .addAttribute(Double::class.java) {
                it.name("review_score").getter(ShopReview::reviewScore::get)
                    .setter(ShopReview::reviewScore::set)
            }
            .addAttribute(EnhancedType.listOf(String::class.java)) {
                it.name("review_photo_list").getter(ShopReview::reviewPhotoList::get)
                    .setter(ShopReview::reviewPhotoList::set)
            }
            .addAttribute(LocalDateTime::class.java) {
                it.name("created_at").getter(ShopReview::createdAt::get)
                    .setter(ShopReview::createdAt::set)
            }
            .addAttribute(LocalDateTime::class.java) {
                it.name("deleted_at").getter(ShopReview::deletedAt::get)
                    .setter(ShopReview::deletedAt::set)
            }
            .build()
    }
}
