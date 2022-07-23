package team.bakkas.domainqueryservice.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domaindynamo.repository.ShopDynamoRepository
import java.time.Duration

/** Cache hit 방식으로 데이터에 access하는 repository 구현
 * @param shopDynamoRepository DynamoDB의 shop 테이블에 접근하는 repository
 * @param shopReactiveRedisTemplate Redis에 Shop entity를 논블로킹 방식으로 캐싱하는데 사용하는 template
 */
@Repository
class ShopRepository(
    private val shopDynamoRepository: ShopDynamoRepository,
    private val shopReactiveRedisTemplate: ReactiveRedisTemplate<String, Shop>
) {
    companion object {
        // Cache를 보관할 기간을 정의
        val DAYS_TO_LIVE = 1L

        fun generateRedisKey(shopId: String, shopName: String) = "shop:${shopId}-${shopName}"
    }

    /**
     * Cache hit 방식으로 DynamoDB로부터 가게를 찾아오는 메소드
     * @param shopId 가게의 Id
     * @param shopName 가게의 이름
     * @return Mono<Shop?>
     */
    fun findShopByIdAndNameWithCaching(shopId: String, shopName: String): Mono<Shop?> {
        val key = generateRedisKey(shopId, shopName)
        val alternativeShopMono: Mono<Shop?> = shopDynamoRepository.findShopByIdAndNameAsync(shopId, shopName)
            .doOnSuccess {
                it?.let {
                    shopReactiveRedisTemplate.opsForValue().set(key, it, Duration.ofDays(DAYS_TO_LIVE))
                        .subscribe()
                }
            }.onErrorResume {
                Mono.empty()
            }
        // Redis에서 key에 해당하는 값을 찾지 못한경우 alternativeShopMono를 이용해 Dynamo에서 찾아온다
        // Dynamo에서 찾아오는데 성공하는 경우 동시에 Redis에 캐싱한다
        return shopReactiveRedisTemplate.opsForValue().get(key)
            .switchIfEmpty(alternativeShopMono)
    }




    // 모든 Shop을 가져오는 flow를 반환해주는 메소드
    fun getAllShopsWithCaching(): Flow<Mono<Shop?>> {
        val shopKeysFlow = shopDynamoRepository.getAllShopKeys() // shop Key Pair들의 flow를 가져온다
        return shopKeysFlow.map {
            findShopByIdAndNameWithCaching(it.first, it.second)
        }
    }
}