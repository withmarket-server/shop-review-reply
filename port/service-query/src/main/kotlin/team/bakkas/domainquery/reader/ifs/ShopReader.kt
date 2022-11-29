package team.bakkas.domainquery.reader.ifs

import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono
import team.bakkas.dynamo.shop.Shop

/**
 * ShopReader
 * Shop Query Business logic을 처리하는 interface
 * Shop domain에 대해서 Facade pattern을 구현하는 interface
 */
interface ShopReader {

    fun findShopById(shopId: String): Mono<Shop>

    fun getAllShops(): Flow<Shop>

    fun getAllShopsWithCaching(): Flow<Shop>
}