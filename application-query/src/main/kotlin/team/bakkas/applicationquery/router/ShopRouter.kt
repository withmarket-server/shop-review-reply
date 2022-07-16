package team.bakkas.applicationquery.router

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.coRouter
import team.bakkas.applicationquery.handler.ShopHandler

@Configuration
class ShopRouter(
    private val shopHandler: ShopHandler
) {

    @Bean
    fun shopRoutes() = coRouter {
        "/v2/shop/simple".nest {
            GET("", shopHandler::findByIdAndName)
        }
    }
}