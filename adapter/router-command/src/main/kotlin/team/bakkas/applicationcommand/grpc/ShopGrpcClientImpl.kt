package team.bakkas.applicationcommand.grpc

import io.grpc.ManagedChannelBuilder
import org.springframework.stereotype.Component
import team.bakkas.applicationcommand.grpc.ifs.ShopGrpcClient
import team.bakkas.grpcIfs.v1.shop.CheckExistShopRequest
import team.bakkas.grpcIfs.v1.shop.CheckExistShopResponse
import team.bakkas.grpcIfs.v1.shop.ShopServiceGrpcKt

// Shop에 대한 Grpc 서비스를 스터빙하는 클래스
@Component
class ShopGrpcClientImpl : ShopGrpcClient {

    companion object {
        // query server를 대상으로 gRPC 포트를 타겟한다
        val channel = ManagedChannelBuilder.forAddress("localhost", 10100)
            .usePlaintext()
            .build()

        // stubbing 객체
        val shopStub = ShopServiceGrpcKt.ShopServiceCoroutineStub(channel)
    }

    /** shopId, shopName에 대응하는 shop이 존재하는지 여부를 반환해주는 메소드
     * @param shopId
     * @param shopName
     * @return CheckExistShopResponse
     */
    override suspend fun isExistShop(shopId: String, shopName: String): CheckExistShopResponse {
        val request = CheckExistShopRequest.newBuilder()
            .setShopId(shopId)
            .setShopName(shopName)
            .build()

        return shopStub.isExistShop(request)
    }
}