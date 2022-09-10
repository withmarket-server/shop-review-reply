package team.bakkas.applicationquery.router

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter
import team.bakkas.applicationquery.handler.ShopQueryHandler
import team.bakkas.common.urls.ServerUrlsInterface

@Configuration
class ShopQueryRouter(
    private val shopHandler: ShopQueryHandler,
    private val uriCOmponent: ServerUrlsInterface
) {

    @Bean
    fun shopRoutes() = coRouter {
        uriCOmponent.SHOP_QUERY_URL.nest {
            GET("", shopHandler::findByIdAndName)
            GET("/list", shopHandler::getAllShops)
        }
    }
}