package team.bakkas.dao.repository.dynamo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.reactive.asFlow
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import team.bakkas.dynamo.shop.Shop
import team.bakkas.dynamo.shop.extensions.softDelete
import team.bakkas.repository.ifs.dynamo.ShopDynamoRepository

/**
 * ShopDynamoRepositoryImpl(private val dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient)
 * shopDynamoRepository의 구현체.
 * @param dynamoDbEnhancedAsyncClient
 * @see <a href="https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html">Developer Guide for DynamoDB SDK</a>
 */
@Repository
class ShopDynamoRepositoryImpl(
    private val dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient
) : ShopDynamoRepository {
    val asyncTable: DynamoDbAsyncTable<Shop> =
        dynamoDbEnhancedAsyncClient.table("shop", Shop.tableSchema)

    override fun findShopById(shopId: String): Mono<Shop> {
        val shopKey = generateKey(shopId)
        return Mono.fromFuture(asyncTable.getItem(shopKey))
            .filter { it.deletedAt == null } // 삭제 처리가 되지않은 shop만 가져온다
    }

    // 모든 Shop에 대한 flow를 반환해주는 메소드
    override fun getAllShops(): Flow<Shop> {
        val shopPublisher = asyncTable.scan().items()
        return shopPublisher.asFlow()
            .filter { it.deletedAt == null } // 삭제 처리가 되지않은 shop만 가져온다
    }

    // shop을 하나 생성해주는 메소드
    override fun createShop(shop: Shop): Mono<Shop> {
        val shopFuture = asyncTable.putItem(shop)
        return Mono.fromFuture(shopFuture)
            .thenReturn(shop)
    }

    // shop을 제거하는 메소드
    override fun deleteShop(shopId: String): Mono<Shop> {
        val deleteShopFuture = asyncTable.deleteItem(generateKey(shopId))
        return Mono.fromFuture(deleteShopFuture)
    }

    // shopId, shopName에 해당하는 shop을 soft delete하는 메소드
    override fun softDeleteShop(shopId: String): Mono<Shop> {
        return findShopById(shopId) // shopId 기반으로 shop을 찾아와서
            .map { it.softDelete() } // shop을 soft delete를 해주고
            .flatMap { createShop(it) } // dynamo에 다시 저장한다
    }

    private fun generateKey(shopId: String): Key = Key.builder()
        .partitionValue(shopId)
        .build()
}