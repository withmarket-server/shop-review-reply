package team.bakkas.applicationquery.grpc

import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import team.bakkas.domainquery.service.ifs.ShopReviewQueryService

// gRPC ShopReview Service에 대한 단위테스트
@ExtendWith(MockKExtension::class)
internal class GrpcShopReviewServiceUnitTest {
    private lateinit var shopReviewQueryService: ShopReviewQueryService

    private lateinit var grpcShopReviewService: GrpcShopReviewService

    @BeforeEach
    fun setUp() {
        shopReviewQueryService = mockk(relaxed = true)
        grpcShopReviewService = spyk(GrpcShopReviewService(shopReviewQueryService))
    }
}