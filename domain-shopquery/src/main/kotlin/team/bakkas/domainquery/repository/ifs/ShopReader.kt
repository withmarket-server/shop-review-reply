package team.bakkas.domainquery.repository.ifs

import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono
import team.bakkas.domaindynamo.entity.Shop

interface ShopReader {

    // Cache hit 방식으로 DynamoDB로부터 가게를 찾아오는 메소드
    fun findShopByIdAndNameWithCaching(shopId: String, shopName: String): Mono<Shop>

    // Redis 상에 존재하는 모든 shop을 가져오는 메소드
    fun getAllShops(): Flow<Shop>
}