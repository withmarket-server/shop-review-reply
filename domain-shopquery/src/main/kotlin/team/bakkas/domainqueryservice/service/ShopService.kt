package team.bakkas.domainqueryservice.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.springframework.core.CoroutinesUtils
import org.springframework.stereotype.Service
import team.bakkas.common.exceptions.ShopNotFoundException
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domainqueryservice.repository.ShopRepository

/** Shop에 대한 비지니스 로직을 구현하는 service layer class
 * @param shopRepository shop에 대한 cache hit이 구현된 repository
 */
@Service
class ShopService(
    private val shopRepository: ShopRepository
) {

    /** shopId와 shopName을 이용해서 shop을 가져오는 service method
     * @param shopId
     * @param shopName
     * @return @NotNull shop
     * @throws ShopNotFoundException
     */
    suspend fun findShopByIdAndName(shopId: String, shopName: String): Shop = withContext(Dispatchers.IO) {
        val shopMono = shopRepository.findShopByIdAndNameWithCaching(shopId, shopName)
        return@withContext CoroutinesUtils.monoToDeferred(shopMono).await()
            ?: throw ShopNotFoundException("Shop is not found!!")
    }

    /** 모든 shop의 리스트를 가져오는 메소드
     * @throws ShopNotFoundException
     * @return list of shop
     */
    suspend fun getAllShopList(): List<Shop> = withContext(Dispatchers.IO) {
        val shopFlow = shopRepository.getAllShopsWithCaching()

        try {
            val firstItem = CoroutinesUtils.monoToDeferred(shopFlow.first()).await()
            checkNotNull(firstItem)
        } catch (e: Exception) {
            throw ShopNotFoundException("Shop is not found!!")
        }

        val shopList = mutableListOf<Shop>()

        shopFlow.buffer()
            .collect {
                val shop = CoroutinesUtils.monoToDeferred(it).await()
                shopList.add(shop!!)
            }

        check(shopList.size != 0) {
            throw ShopNotFoundException("Shop is not found!!")
        }

        shopList
    }
}