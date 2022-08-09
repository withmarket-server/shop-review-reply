package team.bakkas.domaindynamo.repository.dynamo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
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
class ShopDynamoRepositoryImpl(
    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient,
    private val dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient
) {
    // table 변수 선언
    val table: DynamoDbTable<Shop> = dynamoDbEnhancedClient.table("shop", TableSchema.fromBean(Shop::class.java))
    val asyncTable: DynamoDbAsyncTable<Shop> =
        dynamoDbEnhancedAsyncClient.table("shop", TableSchema.fromBean(Shop::class.java))

    // shop을 파라미터로 받아서 table에 shop을 등록시킨 후, 등록된 shop을 그대로 리턴하는 메소드
    fun createShop(shop: Shop): Shop {
        table.putItem(shop)

        return shop
    }

    /* PartitionKey와 SortKey를 이용해서 Item을 가져오는 메소드
     * shop::shopId 형태로 캐싱한다. 그리고 결과값이 null인 경우에는 캐싱하지 않는다
     */
    // @Cacheable(value = ["shop"], key = "#shopId", unless = "#result == null")
    fun findShopByIdAndName(shopId: String, shopName: String): Shop? {
        val shopKey = generateKey(shopId, shopName)

        return table.getItem(shopKey) ?: null
    }

    /* shopId, shopName을 받아서 해당 key에 해당하는 item을 삭제시키는 메소드
     * shop::shopId 형태로 저장된 캐시도 같이 없애버린다
     */
    // @CacheEvict(value = ["shop"], key = "#shopId")
    fun deleteShop(shopId: String, shopName: String): Unit {
        val shopKey = generateKey(shopId, shopName)

        table.deleteItem(shopKey)
    }

    // shop table 상에 존재하는 모든 데이터를 가져오는 메소드

    fun findAllShop(): List<Shop> = table.scan().items().toList()

    /* ==============================[Async Methods]============================== */

    /** shopId와 shopName을 이용해서 비동기식으로 아이템을 가져오는 메소드
     * @param shopId shop의 id
     * @param shopName shop의 이름
     * @return Mono<Shop?>
      */
    fun findShopByIdAndNameAsync(shopId: String, shopName: String): Mono<Shop?> {
        val shopKey = generateKey(shopId, shopName)
        return Mono.fromFuture(asyncTable.getItem(shopKey))
    }

    // 모든 Shop에 대한 key의 flow를 반환해주는 메소드
    fun getAllShopKeys(): Flow<Pair<String, String>> {
        val shopPublisher = asyncTable.scan().items()
        return shopPublisher.asFlow().map {
            Pair(it.shopId, it.shopName)
        }
    }

    // shop을 하나 생성해주는 메소드
    fun createShopAsync(shop: Shop): Mono<Void> {
        val shopFuture = asyncTable.putItem(shop)
        return Mono.fromFuture(shopFuture)
    }

    // shop을 제거하는 메소드
    fun deleteShopAsync(shopId: String, shopName: String): Mono<Shop> {
        val deleteShopFuture = asyncTable.deleteItem(generateKey(shopId, shopName))
        return Mono.fromFuture(deleteShopFuture)
    }

    private fun generateKey(shopId: String, shopName: String): Key = Key.builder()
        .partitionValue(shopId)
        .sortValue(shopName)
        .build()
}