package team.bakkas.dynamo.shop.vo

import software.amazon.awssdk.enhanced.dynamodb.EnhancedType
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean

/** Shop의 위도, 경도 정보를 저장하는 vo
 * @author Doyeop Kim
 * @since 2022/10/13
 */
@DynamoDbBean
data class LatLon(
    @get:DynamoDbAttribute("latitude")
    var latitude: Double = 0.0,
    @get:DynamoDbAttribute("longitude")
    var longitude: Double = 0.0
): java.io.Serializable {

    companion object {
        val tableSchema = TableSchema.fromBean(LatLon::class.java)

        val enhancedType = EnhancedType.documentOf(LatLon::class.java, tableSchema)
    }
}