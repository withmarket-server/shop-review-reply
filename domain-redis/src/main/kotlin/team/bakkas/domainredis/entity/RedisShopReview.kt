package team.bakkas.domainredis.entity

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable
import java.time.LocalDateTime

/** dynamo database의 shop_review를 캐싱하기 위한 엔티티
 * @author Brian
 * @since 22/06/09
 */
@RedisHash("shop_review")
data class RedisShopReview(
    @Id
    var reviewId: String,
    var reviewTitle: String,
    var shopId: String,
    var shopName: String,
    var reviewContent: String,
    var reviewScore: Double,
    var reviewPhotoList: List<String>,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    var createdAt: LocalDateTime,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    var updatedAt: LocalDateTime?
): Serializable
