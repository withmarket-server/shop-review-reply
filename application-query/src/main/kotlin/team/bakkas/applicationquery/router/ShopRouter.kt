package team.bakkas.applicationquery.router

import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter
import team.bakkas.applicationquery.handler.ShopHandler

@Configuration
class ShopRouter(
    private val shopHandler: ShopHandler
) {

    fun shopRoutes(shopHandler: ShopHandler) = coRouter {
        "/v2/shop/simple".nest {
            GET("", shopHandler::findByIdAndName)
        }
    }
}