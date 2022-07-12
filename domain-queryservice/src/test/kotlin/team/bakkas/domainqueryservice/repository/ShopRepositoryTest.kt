package team.bakkas.domainqueryservice.repository

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ReactiveRedisTemplate
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domaindynamo.repository.ShopDynamoRepository

@SpringBootTest
internal class ShopRepositoryTest @Autowired constructor(
    val shopDynamoRepository: ShopDynamoRepository,
    val shopRepository: ShopRepository,
    val shopReactiveRedisTemplate: ReactiveRedisTemplate<String, Shop>
) {


}