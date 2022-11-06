package team.bakkas.applicationcommand.router

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter
import team.bakkas.applicationcommand.handler.HealthCheckHandler

/**
 * @author Doyeop Kim
 * @since 2022/11/06
 */
@Configuration
class HealthCheckRouter(
    private val healthCheckHandler: HealthCheckHandler
) {

    @Bean
    fun healthCheckRoutes() = coRouter {
        "/health".nest {
            GET("", healthCheckHandler::healthCheck)
        }
    }
}