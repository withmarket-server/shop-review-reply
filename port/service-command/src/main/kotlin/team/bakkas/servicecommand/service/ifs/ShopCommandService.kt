package team.bakkas.servicecommand.service.ifs

import reactor.core.publisher.Mono
import team.bakkas.clientcommand.shop.ShopCommand
import team.bakkas.dynamo.shop.Shop

/**
 * ShopCommandService
 * Shop의 Command business logic을 담당하여 처리하는 service interface
 * Clean Architecture 상의 UseCase 역할을 수행한다
 */
interface ShopCommandService {

    fun createShop(shop: Shop): Mono<Shop>

    fun deleteShop(shopId: String): Mono<Shop>

    fun softDeleteShop(shopId: String): Mono<Shop>

    fun applyCreateReview(shopId: String, reviewScore: Double): Mono<Shop>

    fun applyDeleteReview(shopId: String, reviewScore: Double): Mono<Shop>

    fun updateShop(updateRequest: ShopCommand.UpdateRequest): Mono<Shop>
}