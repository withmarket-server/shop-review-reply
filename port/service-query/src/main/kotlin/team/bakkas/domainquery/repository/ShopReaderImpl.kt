package team.bakkas.domainquery.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import team.bakkas.common.utils.RedisUtils
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domainquery.repository.ifs.ShopReader
import team.bakkas.repository.ifs.dynamo.ShopDynamoRepository
import team.bakkas.repository.ifs.redis.ShopRedisRepository

// TODO CQRS 패턴이 완전 정착되면 삭제할 예정임
/** Cache hit 방식으로 데이터에 access하는 repository 구현
 * @param shopDynamoRepository DynamoDB의 shop 테이블에 접근하는 repository
 * @param shopReactiveRedisTemplate Redis에 Shop entity를 논블로킹 방식으로 캐싱하는데 사용하는 template
 */
@Repository
class ShopReaderImpl(
    private val shopDynamoRepository: ShopDynamoRepository,
    private val shopRedisRepository: ShopRedisRepository
) : ShopReader {
    /**
     * Cache hit 방식으로 DynamoDB로부터 가게를 찾아오는 메소드
     * @param shopId 가게의 Id
     * @param shopName 가게의 이름
     * @return Mono<Shop?>
     */
    override fun findShopByIdAndName(shopId: String, shopName: String): Mono<Shop> {
        val key = RedisUtils.generateShopRedisKey(shopId, shopName)
        val alternativeShopMono: Mono<Shop?> = shopDynamoRepository.findShopByIdAndName(shopId, shopName)
            .single()
            .doOnSuccess { shopRedisRepository.cacheShop(it).subscribe() }
            .onErrorResume { Mono.empty() }

        // Redis에서 key에 해당하는 값을 찾지 못한경우 alternativeShopMono를 이용해 Dynamo에서 찾아온다
        // Dynamo에서 찾아오는데 성공하는 경우 동시에 Redis에 캐싱한다
        return shopRedisRepository.findShopByKey(key)
            .switchIfEmpty(alternativeShopMono)
    }

    // Redis로부터 모든 Shop을 가져오는 메소드
    override fun getAllShops(): Flow<Shop> = shopRedisRepository.getAllShops()
}