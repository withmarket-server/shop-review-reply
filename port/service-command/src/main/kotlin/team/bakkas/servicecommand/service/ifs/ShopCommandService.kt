package team.bakkas.servicecommand.service.ifs

import reactor.core.publisher.Mono
import team.bakkas.dynamo.shop.Shop

interface ShopCommandService {

    // shop을 하나 생성하는 메소드
    fun createShop(shop: Shop): Mono<Shop>

    // shop을 하나 삭제하는 메소드
    fun deleteShop(shopId: String): Mono<Shop>

    // shop을 soft delete하는 메소드
    fun softDeleteShop(shopId: String): Mono<Shop>

    // shop에 리뷰 생성을 반영하여 dynamo에 저장해주는 메소드
    fun applyCreateReview(shopId: String, reviewScore: Double): Mono<Shop>

    // shop에 리뷰 삭제를 반영하여 dynamo에 다시 저장해주는 메소드
    fun applyDeleteReview(shopId: String, reviewScore: Double): Mono<Shop>
}