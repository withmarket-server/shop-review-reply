package team.bakkas.common.urls

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("serverdynamo")
class ServerUrls: ServerUrlsInterface {

    override var SHOP_QUERY_SERVER_URL: String = "http://43.200.107.196:10100"
}