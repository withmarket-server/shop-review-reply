package team.bakkas.applicationcommand.grpc.ifs

import team.bakkas.grpcIfs.v1.shop.CheckExistShopResponse

// Shop에 대한 gRPC stubbing을 담당하는 인터페이스
interface ShopGrpcClient {

    suspend fun isExistShop(shopId: String): CheckExistShopResponse
}