package team.bakkas.servicecommand.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import team.bakkas.dynamo.shop.Shop
import team.bakkas.dynamo.shop.usecases.applyReviewCreate
import team.bakkas.dynamo.shop.usecases.applyReviewDelete
import team.bakkas.servicecommand.service.ifs.ShopCommandService
import team.bakkas.repository.ifs.dynamo.ShopDynamoRepository
import team.bakkas.repository.ifs.redis.ShopRedisRepository

/** shop의 command query를 담당하는 비지니스 로직을 정의하는 service 클래스
 * @param shopDynamoRepository dynamoDB에 접근하는데 사용하는 Data access layer의 repository
 * @param shopValidator shop이 올바른지 검증해주는 validator
 */
@Service
class ShopCommandServiceImpl(
    private val shopDynamoRepository: ShopDynamoRepository,
    private val shopRedisRepository: ShopRedisRepository
) : ShopCommandService {
    /** shop을 생성하는 비지니스 로직을 정의하는 메소드
     * @param shop 생성 가능성이 검증된 이후로 들어오는 shop entity 객체
     */
    @Transactional
    override fun createShop(shop: Shop): Mono<Shop> {
        return shopDynamoRepository.createShop(shop)
            .doOnSuccess { shopRedisRepository.cacheShop(it).subscribe() }
    }

    @Transactional
    override fun deleteShop(shopId: String): Mono<Shop> {
        return shopDynamoRepository.deleteShop(shopId) // shop을 dynamo에서 삭제시키고
            .doOnSuccess { shopRedisRepository.deleteShop(shopId).subscribe() } // 삭제에 성공하면 redis에 있는 shop도 삭제
    }

    // Shop을 soft delete하는 메소드
    @Transactional
    override fun softDeleteShop(shopId: String): Mono<Shop> {
        return shopDynamoRepository.softDeleteShop(shopId) // dynamoDB의 data를 soft delete 처리하고
            .doOnSuccess { shopRedisRepository.softDeleteShop(shopId).subscribe() } // Redis의 데이터도 soft delete 처리한다
    }

    // Shop에 review 생성에 의해 생긴 변화를 반영해서 다시 저장해주는 메소드
    @Transactional
    override fun applyCreateReview(shopId: String, reviewScore: Double): Mono<Shop> {
        return shopDynamoRepository.findShopById(shopId) // shopId, shopName을 이용해서 shop을 찾아내고
            .map { it.applyReviewCreate(reviewScore) } // shop에 reviewScore를 반영해서 변화시키고
            .flatMap { shopDynamoRepository.createShop(it) } // 다시 저장한다
            .doOnSuccess { shopRedisRepository.cacheShop(it).subscribe() } // 저장에 성공하면 동시에 redis에 캐싱을 수행한다
    }

    // Shop에 review 삭제에 의해 생긴 변화를 반영해서 다시 저장해주는 메소드
    @Transactional
    override fun applyDeleteReview(shopId: String, reviewScore: Double): Mono<Shop> {
        return shopDynamoRepository.findShopById(shopId) // shopId, shopName을 기반으로 shop을 찾아내고
            .map { it.applyReviewDelete(reviewScore) } // reviewScore를 반영하고
            .flatMap { shopDynamoRepository.createShop(it) } // dynamo에 밀어넣고
            .doOnSuccess { shopRedisRepository.cacheShop(it).subscribe() } // redis에 다시 캐싱한다
    }
}