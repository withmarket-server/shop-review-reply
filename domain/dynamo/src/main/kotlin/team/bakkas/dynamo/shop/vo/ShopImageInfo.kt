package team.bakkas.dynamo.shop.vo

import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean

/** Shop의 이미지와 관련된 정보를 저장하는 vo
 * @author Doyeop Kim
 * @since 2022/10/13
 */
@DynamoDbBean
data class ShopImageInfo(
    @get:DynamoDbAttribute("main_image")
    var mainImage: String? = null,
    @get:DynamoDbAttribute("representative_image_list")
    var representativeImageList: List<String> = listOf()
): java.io.Serializable {

    companion object {
        val tableSchema = TableSchema.fromBean(ShopImageInfo::class.java)

        val enhancedType = EnhancedType.documentOf(ShopImageInfo::class.java, tableSchema)
    }
}