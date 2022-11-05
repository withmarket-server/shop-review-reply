package team.bakkas.applicationquery.grpc

import org.springframework.stereotype.Service
import team.bakkas.domainquery.service.ifs.ShopQueryService
import team.bakkas.grpcIfs.v1.shop.CheckExistShopRequest
import team.bakkas.grpcIfs.v1.shop.CheckExistShopResponse
import team.bakkas.grpcIfs.v1.shop.ShopServiceGrpcKt

@Service
class GrpcShopService(
    private val shopQueryService: ShopQueryService
) : ShopServiceGrpcKt.ShopServiceCoroutineImplBase() {

    /** Shop이 존재하는지 검증해주는 메소드
     * @param request shopId, shopName을 포함한 grpc request 파라미터
     * @throws ShopNotFoundException
     * @return CheckExistShopResponse
     */
    override suspend fun isExistShop(request: CheckExistShopRequest): CheckExistShopResponse {
        val shopId = request.shopId

        val foundShop = shopQueryService.findShopById(shopId)
        val isSatisfied: Boolean = foundShop != null && foundShop.deletedAt == null

        return CheckExistShopResponse.newBuilder()
            .setResult(isSatisfied)
            .build()
    }
}