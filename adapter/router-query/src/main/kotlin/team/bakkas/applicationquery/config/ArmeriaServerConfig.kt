package team.bakkas.applicationquery.config

import com.linecorp.armeria.common.grpc.GrpcSerializationFormats
import com.linecorp.armeria.server.docs.DocService
import com.linecorp.armeria.server.grpc.GrpcService
import com.linecorp.armeria.server.logging.AccessLogWriter
import com.linecorp.armeria.server.logging.ContentPreviewingService
import com.linecorp.armeria.server.logging.LoggingService
import com.linecorp.armeria.spring.ArmeriaServerConfigurator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import team.bakkas.applicationquery.grpc.GrpcShopReviewService
import team.bakkas.applicationquery.grpc.GrpcShopService
import java.nio.charset.StandardCharsets

/** gRPC 활용을 위해 armeria 서버 설정을 하는 configuration class
 * @param grpcShopService gRPC 프로토콜로 제공하는 shop에 관련된 서비스
 * @param grpcShopReviewService gRPC 프로토콜로 제공하는 shopReview에 관련된 서비스
 */
@Configuration
class ArmeriaServerConfig(
    private val grpcShopService: GrpcShopService,
    private val grpcShopReviewService: GrpcShopReviewService
) {

    @Bean
    fun armeriaServerConfigurator(): ArmeriaServerConfigurator {
        return ArmeriaServerConfigurator { serverBuilder ->
            serverBuilder.decorator(LoggingService.newDecorator())
            serverBuilder.decorator(
                ContentPreviewingService.newDecorator(
                    Int.MAX_VALUE,
                    StandardCharsets.UTF_8
                )
            )
            serverBuilder.accessLogWriter(AccessLogWriter.combined(), false)
            serverBuilder.service(
                GrpcService.builder()
                    .addService(grpcShopService)
                    .addService(grpcShopReviewService)
                    .supportedSerializationFormats(GrpcSerializationFormats.values())
                    .enableUnframedRequests(true)
                    .build()
            )
            serverBuilder.serviceUnder("/docs", DocService())
        }
    }
}