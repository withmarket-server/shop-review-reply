package team.bakkas.domaindynamo.repository.redis

import reactor.core.publisher.Mono
import team.bakkas.domaindynamo.entity.Shop

interface ShopRedisRepository {

    // shop을 캐싱하는 메소드
    fun cacheShop(shop: Shop): Mono<Boolean>

    // redis에 저장된 shop을 가져오는 메소드
    fun findShopByKey(shopKey: String): Mono<Shop>
}