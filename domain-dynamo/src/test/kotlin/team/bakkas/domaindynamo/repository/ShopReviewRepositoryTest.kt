package team.bakkas.domaindynamo.repository

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domaindynamo.entity.ShopReview

@SpringBootTest
internal class ShopReviewRepositoryTest @Autowired constructor(
    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient
) {
    // 테이블 정의
    val table: DynamoDbTable<Shop> = dynamoDbEnhancedClient.table("shop", TableSchema.fromBean(Shop::class.java))

    fun createShopWithClient() {

    }

    // 키를 생성하는 메소드
    private fun generateKey(reviewId: String, reviewName: String): Key = Key.builder()
        .partitionValue(reviewId)
        .sortValue(reviewName)
        .build()

    // 리뷰를 하나 생성하는 메소드
    private fun getMockReview(reviewId: String, reviewTitle: String, shopId: String, shopName: String) =
        ShopReview(
            reviewId = reviewId,
            reviewTitle = reviewTitle,
            shopId = shopId,
            shopName = shopName,
            reviewContent = "저는 아주 만족했어요! ^^",
            reviewScore = 10.0,
            reviewPhotoList = listOf()
        )
}