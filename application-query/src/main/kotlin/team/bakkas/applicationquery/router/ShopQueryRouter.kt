package team.bakkas.applicationquery.router

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter
import team.bakkas.applicationquery.handler.ShopQueryHandler

@Configuration
class ShopQueryRouter(
    private val shopHandler: ShopQueryHandler
) {

    @Bean
    fun shopRoutes() = coRouter {
        "/v2/shop/simple".nest {
            GET("", shopHandler::findByIdAndName)
            GET("/list", shopHandler::getAllShops)
        }
    }
}