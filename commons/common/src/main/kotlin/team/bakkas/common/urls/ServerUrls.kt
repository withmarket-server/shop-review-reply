package team.bakkas.common.urls

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("serverdynamo")
class ServerUrls : ServerUrlsInterface {

    override val SHOP_QUERY_SERVER_URL: String = "http://43.200.107.196:10100"

    override val SHOP_QUERY_URL: String = "/v2/shop/simple"
    override val SHOP_REVIEW_URL: String = "/v2/shop-review/simple"
}