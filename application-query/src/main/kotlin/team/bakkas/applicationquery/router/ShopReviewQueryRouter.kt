package team.bakkas.applicationquery.router

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter
import team.bakkas.applicationquery.handler.ShopReviewQueryHandler

@Configuration
class ShopReviewQueryRouter(
    private val shopReviewHandler: ShopReviewQueryHandler
) {

    @Bean
    fun shopReviewRoutes() = coRouter {
        "/v2/shop-review/simple".nest {
            GET("", shopReviewHandler::findReviewByIdAndTitle)
            GET("/list", shopReviewHandler::getReviewListByShopIdAndName)
        }
    }
}