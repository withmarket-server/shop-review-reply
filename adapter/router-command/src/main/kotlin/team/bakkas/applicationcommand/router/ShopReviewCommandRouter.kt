package team.bakkas.applicationcommand.router

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter
import team.bakkas.applicationcommand.handler.ShopReviewCommandHandler

/**
 * ShopReviewCommandRouter
 * shopReview에 대한 client endpoint를 정의하는 router class
 * @param shopReviewCommandHandler
 */
@Configuration
class ShopReviewCommandRouter(
    private val shopReviewCommandHandler: ShopReviewCommandHandler
) {
    @Bean
    fun shopReviewCommandRoutes() = coRouter {
        "/api/v2/shop-review".nest {
            POST("", shopReviewCommandHandler::createReview)
            DELETE("", shopReviewCommandHandler::deleteReview) // [DELETE] localhost:10101/api/v2/shop-review : ShopReview 삭제 (Soft Delete)
        }
    }
}