package team.bakkas.applicationcommand.grpc

import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import team.bakkas.applicationcommand.grpc.ifs.ShopGrpcClient
import team.bakkas.grpcIfs.v1.shop.CheckExistShopRequest
import team.bakkas.grpcIfs.v1.shop.CheckExistShopResponse
import team.bakkas.grpcIfs.v1.shop.ShopServiceGrpcKt

/**
 * ShopGrpcClientImpl
 * ShopGrpcClient의 구현체
 * @param channelHost grpc server's channel host
 */
@Component
class ShopGrpcClientImpl(
    @Value("\${grpc.shop-query}") private val channelHost: String
) : ShopGrpcClient {
    private val channel = ManagedChannelBuilder.forAddress(channelHost, 10100)
        .usePlaintext()
        .build()

    private val shopStub = ShopServiceGrpcKt.ShopServiceCoroutineStub(channel)

    override suspend fun isExistShop(shopId: String): CheckExistShopResponse {
        val request = CheckExistShopRequest.newBuilder()
            .setShopId(shopId)
            .build()

        return shopStub.isExistShop(request)
    }
}