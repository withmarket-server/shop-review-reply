package team.bakkas.domaindynamo.repository.dynamo

import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono
import team.bakkas.domaindynamo.entity.Shop

interface ShopDynamoRepository {

    // shopId와 shopName을 이용해서 비동기식으로 아이템을 가져오는 메소드
    fun findShopByIdAndNameAsync(shopId: String, shopName: String): Mono<Shop>

    // 모든 Shop에 대한 key의 flow를 반환해주는 메소드
    fun getAllShopKeys(): Flow<Pair<String, String>>

    // shop을 하나 생성해주는 메소드
    fun createShopAsync(shop: Shop): Mono<Void>

    // shop을 제거하는 메소드
    fun deleteShopAsync(shopId: String, shopName: String): Mono<Shop>
}