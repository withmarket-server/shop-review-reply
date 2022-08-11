package team.bakkas.common.utils

import org.springframework.web.util.DefaultUriBuilderFactory

object WebClientHelper {

    // 한글 Uri 파싱시 깨짐 문제를 해결하는데 사용하는 메소드
    fun uriBuilderFactory(uri: String): DefaultUriBuilderFactory {
        val factory = DefaultUriBuilderFactory(uri)
        factory.encodingMode = DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY

        return factory
    }
}