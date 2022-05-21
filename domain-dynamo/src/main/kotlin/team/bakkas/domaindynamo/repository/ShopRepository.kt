package team.bakkas.domaindynamo.repository

import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException
import team.bakkas.domaindynamo.entity.Shop

/**
 * Before running this code example, create an Amazon DynamoDB table named shop
 * Also, ensure that you have set up your development environment, including your credentials from your config server
 * For information, see this documentation topic:
 * @see <a href="https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html">Developer Guide for DynamoDB SDK</a>
 * @since 22/05/21
 * @author Brian
 */
@Repository
class ShopRepository(
    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient,
) {
    // table 변수 선언
    val table: DynamoDbTable<Shop> = dynamoDbEnhancedClient.table("shop", TableSchema.fromBean(Shop::class.java))

    // shop을 파라미터로 받아서 table에 shop을 등록시킨 후, 등록된 shop을 그대로 리턴하는 메소드
    fun createShop(shop: Shop): Shop {
        table.putItem(shop)

        return shop
    }

    // PartitionKey와 SortKey를 이용해서 Item을 가져오는 메소드
    fun findShopByIdAndName(shopId: String, shopName: String): Shop? {
        val shopKey = generateKey(shopId, shopName)

        return table.getItem(shopKey) ?: null
    }

    // shopId, shopName을 받아서 해당 key에 해당하는 item을 삭제시키는 메소드
    fun deleteShop(shopId: String, shopName: String): Unit {
        val shopKey = generateKey(shopId, shopName)

        table.deleteItem(shopKey)
    }

    fun generateKey(shopId: String, shopName: String): Key = Key.builder()
        .partitionValue(shopId)
        .sortValue(shopName)
        .build()
}