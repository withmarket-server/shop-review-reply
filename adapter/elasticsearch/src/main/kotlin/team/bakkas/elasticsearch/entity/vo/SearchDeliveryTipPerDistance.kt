package team.bakkas.elasticsearch.entity.vo

import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

/** 거리별 배달료 정보를 저장하는 vo
 * @author Doyeop Kim
 * @since 2022/10/13
 */
data class SearchDeliveryTipPerDistance(
    @field:Field(type = FieldType.Double)
    var distance: Double = 0.0,
    @field:Field(type = FieldType.Integer)
    var price: Int = 0
) {

}