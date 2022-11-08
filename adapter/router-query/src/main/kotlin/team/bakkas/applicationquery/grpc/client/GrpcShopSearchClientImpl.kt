package team.bakkas.applicationquery.grpc.client

import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import team.bakkas.shop.search.ShopSearchGrpc
import team.bakkas.shop.search.ShopSearchGrpc.ShopSearchFutureStub

/**
 * @author Doyeop Kim
 * @since 2022/11/08
 */
class GrpcShopSearchClientImpl(
    @Value("\${grpc.shop-search}") private val channelHost: String
) : GrpcShopSearchClient {

    private val channel = ManagedChannelBuilder.forAddress(channelHost, 9090)
        .usePlaintext()
        .build()

    private val shopSearchStub =
}