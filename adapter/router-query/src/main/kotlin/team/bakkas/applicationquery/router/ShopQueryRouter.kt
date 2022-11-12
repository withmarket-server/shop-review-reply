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
            GET("/simple/list", shopHandler::searchWithIn) // 모든 가게를 가져오는 로직은 무조건 반경 검색이다
            GET("/simple/list/category", shopHandler::searchByCategoryWithIn) // category 반경 검색
            GET("/simple/list/detail-category", shopHandler::searchByDetailCategoryWithIn) // detailCategory 반경 검색
            GET("/simple/list/shop-name", shopHandler::searchByShopNameWithIn) // 가게 이름 기반 검색
            GET("/detail", shopHandler::findDetailById)
        }
    }
}