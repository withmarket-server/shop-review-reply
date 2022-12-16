package team.bakkas.domainquery.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import team.bakkas.dynamo.shop.Shop
import team.bakkas.domainquery.reader.ifs.ShopReader
import team.bakkas.domainquery.service.ifs.ShopQueryService

/**
 * ShopQueryServiceImpl(shopReader: ShopReader)
 * ShopQueryService의 구현체
 */
@Service
class ShopQueryServiceImpl(
    private val shopReader: ShopReader
) : ShopQueryService {

    /*
     * UseCase layer부터는 coroutine을 적용하여 business logic을 수행한다.
     * Dispatchers.IO를 사용하여 I/O 성능을 높인다.
     */
    override suspend fun findShopById(shopId: String): Shop? = withContext(Dispatchers.IO) {
        val shopMono = shopReader.findShopById(shopId)
        return@withContext shopMono.awaitSingleOrNull()
    }

    override suspend fun getAllShopList(): List<Shop> = withContext(Dispatchers.IO) {
        val shopFlow = shopReader.getAllShops()

        val shopList = shopFlow.toList()

        shopList
    }
}