package team.bakkas.common.urls

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("localDynamo")
class LocalUrls : ServerUrlsInterface {

    override val SHOP_QUERY_URL: String = "/api/v2/shop/simple"
    override val SHOP_REVIEW_URL: String = "/api/v2/shop-review/simple"
}