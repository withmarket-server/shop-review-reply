package team.bakkas.dao.repository.dynamo

import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import team.bakkas.dynamo.shopReview.ShopReview
import java.util.*

/** ShopReviewRepository에 대한 Test Class.
 * @author Brian
 * @since 22/06/02
 * @see <a href="https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin/services/dynamodb#code-examples">Query method with Global Secondary Index</a>
 */
@SpringBootTest
internal class ShopReviewDynamoRepositoryTest @Autowired constructor(
    private val dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient,
    private val shopReviewDynamoRepository: ShopReviewDynamoRepositoryImpl
) {
    // 테이블 정의
    val asyncTable = dynamoDbEnhancedAsyncClient.table("shop_review", TableSchema.fromBean(ShopReview::class.java))

    @Test
    @DisplayName("리뷰 작성 테스트")
    fun createReviewTest(): Unit = runBlocking {
        val reviewId = UUID.randomUUID().toString()
        val reviewTitle = "인쇄기가 너무 좋네요!!"
        val shopId = "90223871-8cd0-416f-9a2b-2df4ead38c37"
        val review = getMockReview(reviewId, reviewTitle, shopId)

        // when
        shopReviewDynamoRepository.createReview(review).awaitSingle()
    }


    // 키를 생성하는 메소드
    private fun generateKey(reviewId: String): Key = Key.builder()
        .partitionValue(reviewId)
        .build()

    // 리뷰를 하나 생성하는 메소드
    private fun getMockReview(reviewId: String, reviewTitle: String, shopId: String) =
        ShopReview(
            reviewId = reviewId,
            reviewTitle = reviewTitle,
            shopId = shopId,
            reviewContent = "매우 불만족.",
            reviewScore = 9.0,
            reviewPhotoList = listOf()
        )
}