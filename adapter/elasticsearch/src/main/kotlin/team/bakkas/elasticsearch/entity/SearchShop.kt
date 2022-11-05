package team.bakkas.elasticsearch.entity

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.GeoPointField
import org.springframework.data.elasticsearch.core.geo.GeoPoint
import team.bakkas.dynamo.shop.vo.category.Category
import team.bakkas.dynamo.shop.vo.category.DetailCategory
import team.bakkas.dynamo.shop.vo.sale.Status
import team.bakkas.elasticsearch.entity.vo.SearchDeliveryTipPerDistance

/**
 * @author Brian
 * @since 2022/11/06
 */
@Document(indexName = "shops")
class SearchShop(
    @field:Id
    @field:Field(type = FieldType.Keyword, name = "shop_id")
    var shopId: String = "",
    @field:Field(type = FieldType.Text, name = "shop_name")
    var shopName: String = "",
    @field:Field(type = FieldType.Keyword, name = "status")
    var status: Status = Status.CLOSE,
    @field:GeoPointField
    var location: GeoPoint = GeoPoint(0.0, 0.0),
    @field:Field(type = FieldType.Nested, name = "delivery_tip_per_distance_list")
    var deliveryTipPerDistanceList: List<SearchDeliveryTipPerDistance> = listOf(),
    @field:Field(type = FieldType.Keyword, name = "category")
    var category: Category = Category.ETC,
    @field:Field(type = FieldType.Keyword, name = "detail_category")
    var detailCategory: DetailCategory = DetailCategory.ETC_ALL,
    @field:Field(type = FieldType.Double, name = "average_score")
    var averageScore: Double = 0.0,
    @field:Field(type = FieldType.Double, name = "total_score")
    var totalScore: Double = 0.0,
    @field:Field(type = FieldType.Integer, name = "review_number")
    var reviewNumber: Int = 0,
    @field:Field(type = FieldType.Keyword, name = "business_number")
    var businessNumber: String = ""
) {

}