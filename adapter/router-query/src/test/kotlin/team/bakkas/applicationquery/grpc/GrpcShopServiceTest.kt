package team.bakkas.applicationquery.grpc

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import team.bakkas.domainquery.service.ifs.ShopQueryService

// gRPC Shop Service에 대한 통합테스트
@SpringBootTest
internal class GrpcShopServiceTest @Autowired constructor(
    private val shopQueryService: ShopQueryService
) {

}