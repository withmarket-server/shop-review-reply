package team.bakkas.domainshopcommand.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.core.CoroutinesUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.bakkas.clientcommand.dto.ShopCommand
import team.bakkas.domaindynamo.entity.Shop
import team.bakkas.domaindynamo.repository.dynamo.ShopDynamoRepository
import team.bakkas.domaindynamo.validator.ShopValidator
import team.bakkas.domainshopcommand.extensions.toEntity
import team.bakkas.domainshopcommand.service.ifs.ShopCommandService

/** shop의 command query를 담당하는 비지니스 로직을 정의하는 service 클래스
 * @param shopDynamoRepository dynamoDB에 접근하는데 사용하는 Data access layer의 repository
 * @param shopValidator shop이 올바른지 검증해주는 validator
 */
@Service
class ShopCommandServiceImpl(
    private val shopDynamoRepository: ShopDynamoRepository,
    private val shopValidator: ShopValidator
) : ShopCommandService {

    /** shop을 생성하는 비지니스 로직을 정의하는 메소드
     * @param shopCreateDto shop을 create 하는데 사용하는 dto parameter
     */
    @Transactional
    override suspend fun createShop(shopCreateDto: ShopCommand.ShopCreateDto): Shop = withContext(Dispatchers.IO) {
        val generatedShop = shopCreateDto.toEntity()
        shopValidator.validateCreatable(generatedShop) // 생성 대상인 shop에 대해서 검증을 수행한다
        val shopMono = shopDynamoRepository.createShopAsync(generatedShop)
        val shopDeferred = CoroutinesUtils.monoToDeferred(shopMono)

        shopDeferred.await()

        generatedShop
    }

    // TODO shop을 수정하는 메소드

    // TODO shop을 삭제하는 메소드
}