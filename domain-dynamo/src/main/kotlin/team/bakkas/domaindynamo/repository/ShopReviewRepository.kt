package team.bakkas.domaindynamo.repository

import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import team.bakkas.domaindynamo.entity.ShopReview

/** shop_review 테이블에 대한 repository class
 * @param dynamoDbEnhancedClient
 * @since 22/06/02
 * @author Brian
 */
@Repository
class ShopReviewRepository(
    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient
) {
    // shop_review에 대한 테이블 정의
    val table = dynamoDbEnhancedClient.table("shop_review", TableSchema.fromBean(ShopReview::class.java))

    /** Shop에 대한 Review를 생성하는 메소드
     * @param shopReview 리뷰 엔티티
     */
    fun createReview(shopReview: ShopReview): ShopReview {
        table.putItem(shopReview)

        return shopReview
    }

    /** reviewId와 reviewTitle을 기반으로 review를 하나 찾아오는 메소드
     * @param reviewId
     * @param reviewTitle
     * @return ShopReview if exists else null
     */
    fun findReviewByIdAndTitle(reviewId: String, reviewTitle: String): ShopReview? {
        val reviewKey = generateKey(reviewId, reviewTitle)

        return table.getItem(reviewKey) ?: null
    }

    /** reviewId와 reviewTitle을 기반으로 review를 하나 삭제하는
     * @param reviewId
     * @param reviewTitle
     */
    fun deleteReview(reviewId: String, reviewTitle: String) {
        val reviewKey = generateKey(reviewId, reviewTitle)

        table.deleteItem(reviewKey)
    }

    /** Key를 반환하는 private method
     * @param reviewId Partition Key of shop_review
     * @param reviewTitle Sort Key of shop_review
     * @return key of sjop_review table
     */
    private fun generateKey(reviewId: String, reviewTitle: String) : Key = Key.builder()
        .partitionValue(reviewId)
        .sortValue(reviewTitle)
        .build()
}