package team.bakkas.domainquery.reader

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import team.bakkas.common.utils.RedisUtils
import team.bakkas.dynamo.shop.Shop
import team.bakkas.domainquery.reader.ifs.ShopReader
import team.bakkas.repository.ifs.dynamo.ShopDynamoRepository
import team.bakkas.repository.ifs.redis.ShopRedisRepository

/**
 * ShopReaderImpl(shopDynamoRepository: ShopDynamoRepository, shopRedisRepository: ShopRedisRepository)
 * ShopReader의 구현체. Facade pattern을 구현한다.
 * @param shopDynamoRepository
 * @param shopRedisRepository
 */
@Repository
class ShopReaderImpl(
    private val shopDynamoRepository: ShopDynamoRepository,
    private val shopRedisRepository: ShopRedisRepository
) : ShopReader {

    override fun findShopById(shopId: String): Mono<Shop> {
        val key = RedisUtils.generateShopRedisKey(shopId)

        // redis에 해당 shop이 존재하지 않는 경우 수행하는 Mono
        val alternativeShopMono: Mono<Shop?> = shopDynamoRepository.findShopById(shopId)
            .single()
            .doOnSuccess { shopRedisRepository.cacheShop(it).subscribe() }
            .onErrorResume { Mono.empty() }

        return shopRedisRepository.findShopByKey(key) // redis에서 shop을 찾아보고
            .switchIfEmpty(alternativeShopMono) // 없으면 dynamo를 뒤져서 찾아온다
    }

    override fun getAllShops(): Flow<Shop> {
        return shopRedisRepository.getAllShops()
    }

    override fun getAllShopsWithCaching(): Flow<Shop> {
        return shopDynamoRepository
            .getAllShops() // 모든 shop을 dynamoDB로부터 직접 가져와서
            .map { findShopById(it.shopId).awaitSingle() } // redis에 cache hit 방식으로 저장하며 반환
    }
}