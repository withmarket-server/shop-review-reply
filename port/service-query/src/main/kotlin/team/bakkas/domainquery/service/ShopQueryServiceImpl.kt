package team.bakkas.domainquery.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.bakkas.common.exceptions.shop.ShopNotFoundException
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domainquery.repository.ifs.ShopReader
import team.bakkas.domainquery.service.ifs.ShopQueryService

/** Shop에 대한 비지니스 로직을 구현하는 service layer class
 * @param shopReader shop에 대한 cache hit이 구현된 repository
 */
@Service
class ShopQueryServiceImpl(
    private val shopReader: ShopReader
) : ShopQueryService {

    /** shopId와 shopName을 이용해서 shop을 가져오는 service method
     * @param shopId shop의 primary key
     * @param shopName shop의 sort key
     * @return @NotNull shop
     * @throws ShopNotFoundException
     */
    @Transactional(readOnly = true)
    override suspend fun findShopByIdAndName(shopId: String, shopName: String): Shop? = withContext(Dispatchers.IO) {
        val shopMono = shopReader.findShopByIdAndName(shopId, shopName)
        return@withContext shopMono.awaitSingleOrNull()
    }

    /** 모든 shop의 리스트를 가져오는 메소드
     * @throws ShopNotFoundException
     * @return list of shop
     */
    @Transactional(readOnly = true)
    override suspend fun getAllShopList(): List<Shop> = withContext(Dispatchers.IO) {
        val shopFlow = shopReader.getAllShops()

        val shopList = shopFlow.toList()

        shopList
    }
}