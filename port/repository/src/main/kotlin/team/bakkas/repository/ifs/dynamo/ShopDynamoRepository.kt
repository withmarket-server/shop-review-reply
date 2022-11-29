package team.bakkas.repository.ifs.dynamo

import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono
import team.bakkas.dynamo.shop.Shop

// record system(dynamo)에 접근하는데 사용하는 interface
interface ShopDynamoRepository {

    fun findShopById(shopId: String): Mono<Shop>

    fun getAllShops(): Flow<Shop>

    fun createShop(shop: Shop): Mono<Shop>

    fun deleteShop(shopId: String): Mono<Shop>

    // soft delete 정책에 의해 삭제 처리를 수행하는 메소드
    // record system으로부터 shop을 삭제하지 않고, deletedAt field를 갱신하여 삭제 처리를 수행한다
    fun softDeleteShop(shopId: String): Mono<Shop>
}