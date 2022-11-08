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
        "/api/v2/shop".nest {
            GET("/simple", shopHandler::findById)
            GET("/simple/list", shopHandler::getAllShops)
            GET("/simple/list/category", shopHandler::searchByCategoryWithIn) // category 반경 검색
        }
    }
}