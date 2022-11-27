package team.bakkas.applicationquery.grpc.client

import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import team.bakkas.shop.search.*

/**
 * GrpcShopSearchClientImpl
 * GrpcShopSearchClient의 구현체
 * @param channelHost grpc server's channel host
 * @since 2022/11/09
 */
@Component
class GrpcShopSearchClientImpl(
    @Value("\${grpc.shop-search}") private val channelHost: String
): GrpcShopSearchClient {

    private val channel = ManagedChannelBuilder.forAddress(channelHost, 10200)
        .usePlaintext()
        .build()

    private val searchStub = ShopSearchGrpcKt.ShopSearchCoroutineStub(channel)

    override suspend fun searchCategoryWIthIn(
        category: String,
        latitude: Double,
        longitude: Double,
        distance: Double,
        unit: String,
        page: Int,
        size: Int
    ): SearchResponse {
        val request = SearchCategoryGrpcRequest.newBuilder()
            .setCategory(category)
            .setLatitude(latitude)
            .setLongitude(longitude)
            .setDistance(distance)
            .setUnit(unit)
            .setPage(page)
            .setSize(size)
            .build()

        return searchStub.searchCategoryWithIn(request)
    }

    override suspend fun searchDetailCategoryWithIn(
        detailCategory: String,
        latitude: Double,
        longitude: Double,
        distance: Double,
        unit: String,
        page: Int,
        size: Int
    ): SearchResponse {
        val request = SearchDetailCategoryGrpcRequest.newBuilder()
            .setDetailCategory(detailCategory)
            .setLatitude(latitude)
            .setLongitude(longitude)
            .setDistance(distance)
            .setUnit(unit)
            .setPage(page)
            .setSize(size)
            .build()

        return searchStub.searchDetailCategoryWithIn(request)
    }

    override suspend fun searchShopNameWithIn(
        shopName: String,
        latitude: Double,
        longitude: Double,
        distance: Double,
        unit: String,
        page: Int,
        size: Int
    ): SearchResponse {
        val request = SearchShopNameGrpcRequest.newBuilder()
            .setShopName(shopName)
            .setLatitude(latitude)
            .setLongitude(longitude)
            .setDistance(distance)
            .setUnit(unit)
            .setPage(page)
            .setSize(size)
            .build()

        return searchStub.searchShopNameWithIn(request)
    }

    override suspend fun searchWithIn(
        latitude: Double,
        longitude: Double,
        distance: Double,
        unit: String,
        page: Int,
        size: Int
    ): SearchResponse {
        val request = SearchWithInGrpcRequest.newBuilder()
            .setLatitude(latitude)
            .setLongitude(longitude)
            .setDistance(distance)
            .setUnit(unit)
            .setPage(page)
            .setSize(size)
            .build()

        return searchStub.searchWithIn(request)
    }
}