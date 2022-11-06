package team.bakkas.applicationquery.handler

import kotlinx.coroutines.coroutineScope
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait

/**
 * @author Doyeop Kim
 * @since 2022/11/06
 */
@Component
class HealthCheckHandler {

    suspend fun healthCheck(request: ServerRequest): ServerResponse = coroutineScope {
        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait("healthy")
    }
}