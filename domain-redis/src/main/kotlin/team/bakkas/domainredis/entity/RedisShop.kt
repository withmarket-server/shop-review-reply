package team.bakkas.domainredis.entity

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import team.bakkas.common.category.Category
import team.bakkas.common.category.DetailCategory
import java.io.Serializable
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/** dynamo database의 shop을 캐싱하기 위한 entity class
 * @author Brian
 * @since 22/06/09
 */
@RedisHash("shop")
data class RedisShop(
    @Id
    var shopId: String,
    var shopName: String,
    var isOpen: Boolean,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    var openTime: LocalDateTime,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    var closeTime: LocalDateTime,
    var lotNumberAddress: String,
    var roadNameAddress: String,
    var latitude: Double,
    var longitude: Double,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    var createdAt: LocalDateTime,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    var updatedAt: LocalDateTime,
    var averageScore: Double,
    var reviewNumber: Int,
    var mainImage: String,
    var representativeImageList: List<String>,
    var shopDescription: String?,
    var shopCategory: Category,
    var shopDetailCategory: DetailCategory
): Serializable