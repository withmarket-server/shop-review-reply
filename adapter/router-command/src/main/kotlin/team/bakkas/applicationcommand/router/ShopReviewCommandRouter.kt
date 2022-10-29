package team.bakkas.applicationcommand.router

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter
import team.bakkas.applicationcommand.handler.ShopReviewCommandHandler

// ShopReview에 대한 command end-point를 정의한 Router class
@Configuration
class ShopReviewCommandRouter(
    private val shopReviewCommandHandler: ShopReviewCommandHandler
) {
    @Bean
    fun shopReviewCommandRoutes() = coRouter {
        "/v2/shop-review".nest {
            POST("", shopReviewCommandHandler::createReview)
            DELETE("", shopReviewCommandHandler::deleteReview) // [DELETE] localhost:10101/api/v2/shop-review : ShopReview 삭제 (Soft Delete)
        }
    }
}