package team.bakkas.applicationquery.router

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter
import team.bakkas.applicationquery.handler.ShopQueryHandler
import team.bakkas.common.urls.ServerUrlsInterface

@Configuration
class ShopQueryRouter(
    private val shopHandler: ShopQueryHandler,
    private val uriComponent: ServerUrlsInterface
) {

    @Bean
    fun shopRoutes() = coRouter {
        uriComponent.SHOP_QUERY_URL.nest {
            GET("", shopHandler::findById)
            GET("/list", shopHandler::getAllShops)
        }
    }
}