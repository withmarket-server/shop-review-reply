package team.bakkas.repository.ifs.dynamo

import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono
import team.bakkas.dynamo.shop.Shop

interface ShopDynamoRepository {

    // shopId와 shopName을 이용해서 비동기식으로 아이템을 가져오는 메소드
    fun findShopByIdAndName(shopId: String, shopName: String): Mono<Shop>

    fun getAllShops(): Flow<Shop>

    // shop을 하나 생성해주는 메소드
    fun createShop(shop: Shop): Mono<Void>

    // shop을 제거하는 메소드
    fun deleteShop(shopId: String, shopName: String): Mono<Shop>
}