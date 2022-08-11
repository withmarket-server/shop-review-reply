package team.bakkas.common.urls

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("localdynamo")
class LocalUrls : ServerUrlsInterface {

    override var SHOP_QUERY_SERVER_URL: String = "http://localhost:10100"
}