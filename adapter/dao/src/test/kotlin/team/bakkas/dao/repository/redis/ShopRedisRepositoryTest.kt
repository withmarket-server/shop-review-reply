package team.bakkas.dao.repository.redis

import io.kotest.common.runBlocking
import kotlinx.coroutines.reactor.awaitSingle
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import team.bakkas.common.utils.RedisUtils
import team.bakkas.repository.ifs.dynamo.ShopDynamoRepository
import team.bakkas.repository.ifs.redis.ShopRedisRepository

@SpringBootTest
internal class ShopRedisRepositoryTest @Autowired constructor(
    private val shopDynamoRepository: ShopDynamoRepository,
    private val shopRedisRepository: ShopRedisRepository
) {

}