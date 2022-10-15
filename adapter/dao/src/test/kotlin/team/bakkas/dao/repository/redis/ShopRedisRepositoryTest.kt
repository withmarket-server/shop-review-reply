package team.bakkas.dao.repository.redis

import io.kotest.common.runBlocking
import kotlinx.coroutines.reactor.awaitSingle
import org.junit.jupiter.api.Assertions.*
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
    @DisplayName("[cacheShop] shop cache 테스트")
    fun cacheShopTest(): Unit = runBlocking {
        // given
        val shopId = "eddb27c4-ca3e-4aac-9b4d-a3c453842a3f"
        val shopName = "포스마트"
        val targetShop = shopDynamoRepository.findShopByIdAndName(shopId, shopName).awaitSingle()

        // when
        shopRedisRepository.cacheShop(targetShop).awaitSingle()

        // then
        val shopKey = RedisUtils.generateShopRedisKey(shopId, shopName)
        val cachedShop = shopRedisRepository.findShopByKey(shopKey).awaitSingle()

        with(cachedShop) {
            println(this.shopId)
            println(this.shopName)
            println(this.addressInfo.roadNameAddress)
        }
    }
}