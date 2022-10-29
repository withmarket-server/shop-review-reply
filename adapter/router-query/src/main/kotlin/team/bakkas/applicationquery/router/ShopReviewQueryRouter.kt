package team.bakkas.applicationquery.router

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter
import team.bakkas.applicationquery.handler.ShopReviewQueryHandler
import team.bakkas.common.urls.ServerUrlsInterface

@Configuration
class ShopReviewQueryRouter(
    private val shopReviewHandler: ShopReviewQueryHandler,
    private val uriComponent: ServerUrlsInterface
) {

    @Bean
    fun shopReviewRoutes() = coRouter {
        uriComponent.SHOP_REVIEW_URL.nest {
            GET("", shopReviewHandler::findReviewByIdAndTitle)
            GET("/list", shopReviewHandler::getReviewListByShopId)
        }
    }
}