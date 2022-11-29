package team.bakkas.repository.ifs.redis

import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono
import team.bakkas.dynamo.shop.Shop

/**
 * ShopRedisRepository
 * Redis에 접근하여 Shop을 제어하는데 사용하는 interface
 */
interface ShopRedisRepository {

    fun cacheShop(shop: Shop): Mono<Shop>

    fun findShopByKey(shopKey: String): Mono<Shop>

    fun getAllShops(): Flow<Shop>

    fun deleteShop(shopId: String): Mono<Boolean>
}