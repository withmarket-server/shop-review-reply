package team.bakkas.domainqueryservice.repository

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domaindynamo.repository.ShopDynamoRepository

/** Cache hit 방식으로 데이터에 access하는 repository 구현
 * @param shopDynamoRepository DynamoDB의 shop 테이블에 접근하는 repository
 * @param shopReactiveRedisTemplate Redis에 Shop entity를 논블로킹 방식으로 캐싱하는데 사용하는 template
 */
@Repository
class ShopRepository(
    private val shopDynamoRepository: ShopDynamoRepository,
    private val shopReactiveRedisTemplate: ReactiveRedisTemplate<String, Shop>
) {

}