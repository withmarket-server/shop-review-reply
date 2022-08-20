package team.bakkas.infrastructure.repository.dynamo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domaindynamo.repository.dynamo.ShopDynamoRepository

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
    private val dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient
) : ShopDynamoRepository {
    val asyncTable: DynamoDbAsyncTable<Shop> =
        dynamoDbEnhancedAsyncClient.table("shop", TableSchema.fromBean(Shop::class.java))

    /* ==============================[Async Methods]============================== */

    /** shopId와 shopName을 이용해서 비동기식으로 아이템을 가져오는 메소드
     * @param shopId shop의 id
     * @param shopName shop의 이름
     * @return Mono<Shop?>
     */
    override fun findShopByIdAndNameAsync(shopId: String, shopName: String): Mono<Shop> {
        val shopKey = generateKey(shopId, shopName)
        return Mono.fromFuture(asyncTable.getItem(shopKey))
    }

    // 모든 Shop에 대한 key의 flow를 반환해주는 메소드
    override fun getAllShopKeys(): Flow<Pair<String, String>> {
        val shopPublisher = asyncTable.scan().items()
        return shopPublisher.asFlow()
            .map { Pair(it.shopId, it.shopName) }
    }

    // 모든 Shop에 대한 flow를 반환해주는 메소드
    override fun getAllShops(): Flow<Shop> {
        val shopPublisher = asyncTable.scan().items()
        return shopPublisher.asFlow()
    }

    // shop을 하나 생성해주는 메소드
    override fun createShopAsync(shop: Shop): Mono<Void> {
        val shopFuture = asyncTable.putItem(shop)
        return Mono.fromFuture(shopFuture)
    }

    // shop을 제거하는 메소드
    override fun deleteShopAsync(shopId: String, shopName: String): Mono<Shop> {
        val deleteShopFuture = asyncTable.deleteItem(generateKey(shopId, shopName))
        return Mono.fromFuture(deleteShopFuture)
    }

    private fun generateKey(shopId: String, shopName: String): Key = Key.builder()
        .partitionValue(shopId)
        .sortValue(shopName)
        .build()
}