package team.bakkas.applicationquery.grpc.client

import team.bakkas.shop.search.SearchResponse

/**
 * GrpcShopSearchClient
 * 가게 검색 client를 정의하는 interface
 * @since 2022/11/08
 */
interface GrpcShopSearchClient {

    // 반경 내 카테고리 검색 메소드
    suspend fun searchCategoryWIthIn(
        category: String,
        latitude: Double,
        longitude: Double,
        distance: Double,
        unit: String,
        page: Int,
        size: Int
    ): SearchResponse

    // 반경 내 세부 카테고리 검색 메소드
    suspend fun searchDetailCategoryWithIn(
        detailCategory: String,
        latitude: Double,
        longitude: Double,
        distance: Double,
        unit: String,
        page: Int,
        size: Int
    ): SearchResponse

    // 반경 내 가게 이름 기반 검색 메소드
    suspend fun searchShopNameWithIn(
        shopName: String,
        latitude: Double,
        longitude: Double,
        distance: Double,
        unit: String,
        page: Int,
        size: Int
    ): SearchResponse

    // 반경 내 모든 가게 검색 메소드
    suspend fun searchWithIn(
        latitude: Double,
        longitude: Double,
        distance: Double,
        unit: String,
        page: Int,
        size: Int
    ): SearchResponse
}