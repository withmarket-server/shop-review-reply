package team.bakkas.dynamo.shop.vo

import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import team.bakkas.dynamo.shop.vo.category.Category
import team.bakkas.dynamo.shop.vo.category.DetailCategory

/** Shop의 카테고리 정보를 저장하는 vo
 * @author Doyeop Kim
 * @since 2022/10/13
 */
@DynamoDbBean
data class CategoryInfo(
    @get:DynamoDbAttribute("shop_category")
    var shopCategory: Category = Category.ETC,
    @get:DynamoDbAttribute("shop_detail_category")
    var shopDetailCategory: DetailCategory = DetailCategory.ETC_ALL
): java.io.Serializable {

    companion object {
        val tableSchema = TableSchema.fromBean(CategoryInfo::class.java)

        val enhancedType = EnhancedType.documentOf(CategoryInfo::class.java, tableSchema)
    }
}