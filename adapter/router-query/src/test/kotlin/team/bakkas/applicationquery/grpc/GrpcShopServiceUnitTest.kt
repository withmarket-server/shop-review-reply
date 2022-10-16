package team.bakkas.applicationquery.grpc

import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import team.bakkas.domainquery.service.ifs.ShopQueryService

// gRPC shop service에 대한 단위테스트
@ExtendWith(MockKExtension::class)
internal class GrpcShopServiceUnitTest {
    private lateinit var shopQueryService: ShopQueryService

    private lateinit var grpcShopService: GrpcShopService

    @BeforeEach
    fun setUp() {
        shopQueryService = mockk(relaxed = true)
        grpcShopService = spyk(GrpcShopService(shopQueryService))
    }
}