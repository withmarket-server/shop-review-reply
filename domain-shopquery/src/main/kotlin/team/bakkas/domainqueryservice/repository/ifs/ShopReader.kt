package team.bakkas.domainqueryservice.repository.ifs

import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono
import team.bakkas.domaindynamo.entity.Shop

interface ShopReader {

    // Cache hit 방식으로 DynamoDB로부터 가게를 찾아오는 메소드
    fun findShopByIdAndNameWithCaching(shopId: String, shopName: String): Mono<Shop?>

    // 모든 Shop을 가져오는 flow를 반환해주는 메소드
    fun getAllShopsWithCaching(): Flow<Mono<Shop?>>
}