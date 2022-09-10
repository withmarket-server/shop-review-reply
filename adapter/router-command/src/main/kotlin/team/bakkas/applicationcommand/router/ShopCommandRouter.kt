package team.bakkas.applicationcommand.router

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter
import team.bakkas.applicationcommand.handler.ShopCommandHandler

@Configuration
class ShopCommandRouter(
    private val shopCommandHandler: ShopCommandHandler
) {

    @Bean
    fun shopCommandRoutes() = coRouter {
        "/v2/shop".nest {
            POST("", shopCommandHandler::createShop) // [POST] localhost:10101/v2/shop : shopDto로 들어온 요청을 shop으로 생성한다
        }
    }
}