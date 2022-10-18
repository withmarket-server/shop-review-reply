package team.bakkas.servicecommand.service.ifs

import reactor.core.publisher.Mono
import team.bakkas.dynamo.shop.Shop

interface ShopCommandService {

    // shop을 하나 생성하는 메소드
    fun createShop(shop: Shop): Mono<Shop>

    // shop에 리뷰 생성을 반영하여 dynamo에 저장해주는 메소드
    fun applyCreateReview(shopId: String, shopName: String, reviewScore: Double): Mono<Shop>

    // shop에 리뷰 삭제를 반영하여 dynamo에 다시 저장해주는 메소드
    fun applyDeleteReview(shopId: String, shopName: String, reviewScore: Double): Mono<Shop>
}