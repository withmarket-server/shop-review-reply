package team.bakkas.dao.repository.redis

import io.kotest.common.runBlocking
import kotlinx.coroutines.flow.toList
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

    @Test
    @DisplayName("모든 shop을 찾아오는 테스트")
    fun getAllShopsTest(): Unit = runBlocking {
        val shopList = shopRedisRepository.getAllShops().toList()

        shopList.forEach {
            println(it.shopId)
            println(it.shopName)
        }
    }

    @Test
    @DisplayName("가게를 redis에서 soft delete하는 테스트")
    fun softDeleteTest(): Unit = runBlocking {
        val shopId = "90223871-8cd0-416f-9a2b-2df4ead38c37"

        val shop = shopRedisRepository.softDeleteShop(shopId).awaitSingle()

        println(shop.shopId)
        println(shop.deletedAt)
    }
}