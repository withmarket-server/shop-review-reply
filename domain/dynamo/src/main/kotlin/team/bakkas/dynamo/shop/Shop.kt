package team.bakkas.dynamo.shop

import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import team.bakkas.dynamo.BaseTimeEntity
import team.bakkas.dynamo.shop.vo.*
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

/** Shop Entity class with DynamoDB
 * @author Brian
 * @since 22/05/19
 */
@DynamoDbBean
class Shop(
    var shopId: String = UUID.randomUUID().toString(), // PK
    var shopName: String = "", // Sort Key
    var salesInfo: SalesInfo = SalesInfo(), // 영업 관련 정보
    var addressInfo: AddressInfo = AddressInfo(), // 주소 정보
    var latLon: LatLon = LatLon(), // 위도, 경도 정보
    var shopImageInfo: ShopImageInfo = ShopImageInfo(), // 가게의 이미지 정보
    var branchInfo: BranchInfo = BranchInfo(), // 분점 정보
    var categoryInfo: CategoryInfo = CategoryInfo(), // 카테고리 정보
    var deliveryTipPerDistanceList: List<DeliveryTipPerDistance> = listOf(),  // 배달 거리별 팁 정보
    var totalScore: Double = 0.0, // 가게의 총점
    var reviewNumber: Int = 0, // 리뷰의 개수
    var shopDescription: String? = null // 가게에 대한 설명
) : Serializable, BaseTimeEntity() {

    companion object {
        val tableSchema = TableSchema.builder(Shop::class.java)
            .newItemSupplier(::Shop)
            .addAttribute(String::class.java) {
                it.name("shop_id").getter(Shop::shopId::get)
                    .setter(Shop::shopId::set)
                    .tags(StaticAttributeTags.primaryPartitionKey())
            }
            .addAttribute(String::class.java) {
                it.name("shop_name").getter(Shop::shopName::get)
                    .setter(Shop::shopName::set)
                    .tags(StaticAttributeTags.primarySortKey())
            }
            .addAttribute(SalesInfo.enhancedType) {
                it.name("sales_info").getter(Shop::salesInfo::get)
                    .setter(Shop::salesInfo::set)
            }
            .addAttribute(AddressInfo.enhancedType) {
                it.name("address_info").getter(Shop::addressInfo::get)
                    .setter(Shop::addressInfo::set)
            }
            .addAttribute(LatLon.enhancedType) {
                it.name("lat_lon").getter(Shop::latLon::get)
                    .setter(Shop::latLon::set)
            }
            .addAttribute(ShopImageInfo.enhancedType) {
                it.name("shop_image_info").getter(Shop::shopImageInfo::get)
                    .setter(Shop::shopImageInfo::set)
            }
            .addAttribute(BranchInfo.enhancedType) {
                it.name("branch_info").getter(Shop::branchInfo::get)
                    .setter(Shop::branchInfo::set)
            }
            .addAttribute(CategoryInfo.enhancedType) {
                it.name("category_info").getter(Shop::categoryInfo::get)
                    .setter(Shop::categoryInfo::set)
            }
            .addAttribute(EnhancedType.listOf(DeliveryTipPerDistance.enhancedType)) {
                it.name("delivery_tip_per_distance_list").getter(Shop::deliveryTipPerDistanceList::get)
                    .setter(Shop::deliveryTipPerDistanceList::set)
            }
            .addAttribute(Double::class.java) {
                it.name("total_score").getter(Shop::totalScore::get)
                    .setter(Shop::totalScore::set)
            }
            .addAttribute(Int::class.java) {
                it.name("review_number").getter(Shop::reviewNumber::get)
                    .setter(Shop::reviewNumber::set)
            }
            .addAttribute(String::class.java) {
                it.name("shop_description").getter(Shop::shopDescription::get)
                    .setter(Shop::shopDescription::set)
            }
            .addAttribute(LocalDateTime::class.java) {
                it.name("created_at").getter(Shop::createdAt::get)
                    .setter(Shop::createdAt::set)
            }
            .addAttribute(LocalDateTime::class.java) {
                it.name("updated_at").getter(Shop::updatedAt::get)
                    .setter(Shop::updatedAt::set)
            }
            .build()
    }
}