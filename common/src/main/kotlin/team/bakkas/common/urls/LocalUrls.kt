package team.bakkas.common.urls

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("localdynamo")
class LocalUrls : ServerUrlsInterface {

    override val SHOP_QUERY_SERVER_URL: String = "http://localhost:10100"

    override val SHOP_QUERY_URL: String = "/v2/shop/simple"
    override val SHOP_REVIEW_URL: String = "/v2/shop-review/simple"
}