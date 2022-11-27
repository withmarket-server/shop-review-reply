package team.bakkas.applicationquery.grpc.server

import org.springframework.stereotype.Service
import team.bakkas.domainquery.service.ifs.ShopQueryService
import team.bakkas.grpcIfs.v1.shop.CheckExistShopRequest
import team.bakkas.grpcIfs.v1.shop.CheckExistShopResponse
import team.bakkas.grpcIfs.v1.shop.ShopServiceGrpcKt

/**
 * GrpcShopService
 * shop에 대한 grpc service class
 * @param shopQueryService
 */
@Service
class GrpcShopService(
    private val shopQueryService: ShopQueryService
) : ShopServiceGrpcKt.ShopServiceCoroutineImplBase() {

    override suspend fun isExistShop(request: CheckExistShopRequest): CheckExistShopResponse {
        val shopId = request.shopId

        val foundShop = shopQueryService.findShopById(shopId)

        // 찾아온 shop이 실제로 존재하며, 삭제된 적이 없는 shop이여야만한다
        val isSatisfied: Boolean = foundShop != null && foundShop.deletedAt == null

        return CheckExistShopResponse.newBuilder()
            .setResult(isSatisfied)
            .build()
    }
}