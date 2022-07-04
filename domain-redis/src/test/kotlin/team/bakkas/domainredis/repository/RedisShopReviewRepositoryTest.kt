package team.bakkas.domainredis.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class RedisShopReviewRepositoryTest @Autowired constructor(
    private val redisShopReviewRepository: RedisShopReviewRepository
) {

}