package team.bakkas.applicationquery.router

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter
import team.bakkas.applicationquery.handler.ShopReviewQueryHandler

/**
 * ShopReviewQueryRouter
 * shopReview에 대한 query client endpoint를 정의하는 router class
 * @param shopReviewHandler
 */
@Configuration
class ShopReviewQueryRouter(
    private val shopReviewHandler: ShopReviewQueryHandler
) {

    @Bean
    fun shopReviewRoutes() = coRouter {
        "/api/v2/shop-review/simple".nest {
            GET("", shopReviewHandler::findReviewById)
            GET("/list", shopReviewHandler::getReviewListByShopId)
        }
    }
}