package team.bakkas.applicationquery.handler

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok
import team.bakkas.applicationquery.extensions.toSimpleResponse
import team.bakkas.applicationquery.grpc.client.GrpcShopSearchClient
import team.bakkas.common.ResultFactory
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.shop.CategoryNotFoundException
import team.bakkas.common.exceptions.shop.DetailCategoryNotFoundException
import team.bakkas.common.exceptions.shop.ShopNotFoundException
import team.bakkas.domainquery.service.ifs.ShopQueryService
import team.bakkas.dynamo.shop.vo.category.Category
import team.bakkas.dynamo.shop.vo.category.DetailCategory

/**
 * ShopQueryHandler
 * Shop에 대한 query request를 처리하여 검증하고, 검증 완료 시 결과를 반환하는 handler class
 * @param shopQueryService shop에 대한 Service 로직들을 저장한 컨포넌트
 * @param grpcShopSearchClient ES에서 조건을 검색하기 위한 grpc client
 */
@Component
class ShopQueryHandler(
    private val shopQueryService: ShopQueryService,
    private val grpcShopSearchClient: GrpcShopSearchClient
) {

    suspend fun findById(request: ServerRequest): ServerResponse = coroutineScope {
        val shopId = request.queryParamOrNull("id") ?: throw RequestParamLostException("shopId is lost")

        check(shopId.isNotEmpty()) {
            throw RequestParamLostException("Empty query parameter")
        }

        val shop = shopQueryService.findShopById(shopId) ?: throw ShopNotFoundException("Shop is not found!!")

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getSingleResult(shop.toSimpleResponse()))
    }

    suspend fun findDetailById(request: ServerRequest): ServerResponse = coroutineScope {
        val shopId = request.queryParamOrNull("id") ?: throw RequestParamLostException("shopId is lost")

        check(shopId.isNotEmpty()) {
            throw RequestParamLostException("Empty query parameter")
        }

        val shop = shopQueryService.findShopById(shopId) ?: throw ShopNotFoundException("Shop is not found!!")

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getSingleResult(shop))
    }

    suspend fun getAllShops(request: ServerRequest): ServerResponse = coroutineScope {
        val shopList = shopQueryService.getAllShopList()

        // shop이 redis에서 하나도 발견되지 않은 경우 예외 처리
        check(shopList.isNotEmpty()) {
            throw ShopNotFoundException("Shop is not found!!")
        }

        val shopResponseList = shopList.map { it.toSimpleResponse() }

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getMultipleResult(shopResponseList))
    }

    // 카테고리 반경 검색
    suspend fun searchByCategoryWithIn(request: ServerRequest): ServerResponse = coroutineScope {
        val category = request.queryParamOrNull("category") ?: throw RequestParamLostException("category is lost")
        val latitude = request.queryParamOrNull("latitude") ?: throw RequestParamLostException("latitude is lost")
        val longitude = request.queryParamOrNull("longitude") ?: throw RequestParamLostException("longitude is lost")
        val distance = request.queryParamOrNull("distance") ?: throw RequestParamLostException("distance is lost")
        val unit = request.queryParamOrNull("unit") ?: throw RequestParamLostException("unit is lost")
        val page = request.queryParamOrNull("page") ?: throw RequestParamLostException("page is lost")
        val size = request.queryParamOrNull("size") ?: throw RequestParamLostException("size is lost")

        // 카테고리가 존재하는지 검증
        if (category !in Category.values().map { it.toString() }) {
            throw CategoryNotFoundException("Category is not valid")
        }

        // 조건에 만족하는 shop의 목록을 가져온다
        val satisfiedShopIdFlow = grpcShopSearchClient.searchCategoryWIthIn(
            category,
            latitude.toDouble(),
            longitude.toDouble(),
            distance.toDouble(),
            unit,
            page.toInt(),
            size.toInt()
        ).idsList.asFlow()

        val shopResponseList = satisfiedShopIdFlow
            .map { shopQueryService.findShopById(it)!! }
            .map { it.toSimpleResponse() } // 간략화된 shop 정보로 변환
            .toList()

        check(shopResponseList.isNotEmpty()) {
            throw ShopNotFoundException("Shop is not found!!")
        }

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getMultipleResult(shopResponseList))
    }

    // detail-category 기반의 반경 검색
    suspend fun searchByDetailCategoryWithIn(request: ServerRequest): ServerResponse = coroutineScope {
        val detailCategory =
            request.queryParamOrNull("detail-category") ?: throw RequestParamLostException("category is lost")
        val latitude = request.queryParamOrNull("latitude") ?: throw RequestParamLostException("latitude is lost")
        val longitude = request.queryParamOrNull("longitude") ?: throw RequestParamLostException("longitude is lost")
        val distance = request.queryParamOrNull("distance") ?: throw RequestParamLostException("distance is lost")
        val unit = request.queryParamOrNull("unit") ?: throw RequestParamLostException("unit is lost")
        val page = request.queryParamOrNull("page") ?: throw RequestParamLostException("page is lost")
        val size = request.queryParamOrNull("size") ?: throw RequestParamLostException("size is lost")

        // detail category가 실존하는지 검증
        if (detailCategory !in DetailCategory.values().map { it.toString() }) {
            throw DetailCategoryNotFoundException("detail-category is lost")
        }

        val satisfiedShopIdFlow = grpcShopSearchClient.searchDetailCategoryWithIn(
            detailCategory,
            latitude.toDouble(),
            longitude.toDouble(),
            distance.toDouble(),
            unit,
            page.toInt(),
            size.toInt()
        ).idsList.asFlow()

        val shopResponseList = satisfiedShopIdFlow
            .map { shopQueryService.findShopById(it)!! }
            .map { it.toSimpleResponse() }
            .toList()

        check(shopResponseList.isNotEmpty()) {
            throw ShopNotFoundException("Shop is not found!!")
        }

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getMultipleResult(shopResponseList))
    }

    // 가게이름 + 반경
    suspend fun searchByShopNameWithIn(request: ServerRequest): ServerResponse = coroutineScope {
        val shopName = request.queryParamOrNull("shop-name") ?: throw RequestParamLostException("shop-name is lost")
        val latitude = request.queryParamOrNull("latitude") ?: throw RequestParamLostException("latitude is lost")
        val longitude = request.queryParamOrNull("longitude") ?: throw RequestParamLostException("longitude is lost")
        val distance = request.queryParamOrNull("distance") ?: throw RequestParamLostException("distance is lost")
        val unit = request.queryParamOrNull("unit") ?: throw RequestParamLostException("unit is lost")
        val page = request.queryParamOrNull("page") ?: throw RequestParamLostException("page is lost")
        val size = request.queryParamOrNull("size") ?: throw RequestParamLostException("size is lost")

        // 2글자 이상으로만 검색을 허용
        check(shopName.length >= 2) {
            throw RequestParamLostException("두 글자 이상으로만 검색을 허용합니다.")
        }

        val satisfiedShopIdFlow = grpcShopSearchClient.searchShopNameWithIn(
            shopName,
            latitude.toDouble(),
            longitude.toDouble(),
            distance.toDouble(),
            unit,
            page.toInt(),
            size.toInt()
        ).idsList.asFlow()

        val shopResponseList = satisfiedShopIdFlow
            .map { shopQueryService.findShopById(it)!! }
            .map { it.toSimpleResponse() }
            .toList()

        check(shopResponseList.isNotEmpty()) {
            throw ShopNotFoundException("Shop is not found!!")
        }

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getMultipleResult(shopResponseList))
    }

    // 반경 검색
    suspend fun searchWithIn(request: ServerRequest): ServerResponse = coroutineScope {
        val latitude = request.queryParamOrNull("latitude") ?: throw RequestParamLostException("latitude is lost")
        val longitude = request.queryParamOrNull("longitude") ?: throw RequestParamLostException("longitude is lost")
        val distance = request.queryParamOrNull("distance") ?: throw RequestParamLostException("distance is lost")
        val unit = request.queryParamOrNull("unit") ?: throw RequestParamLostException("unit is lost")
        val page = request.queryParamOrNull("page") ?: throw RequestParamLostException("page is lost")
        val size = request.queryParamOrNull("size") ?: throw RequestParamLostException("size is lost")

        val satisfiedShopIdFlow = grpcShopSearchClient.searchWithIn(
            latitude.toDouble(),
            longitude.toDouble(),
            distance.toDouble(),
            unit,
            page.toInt(),
            size.toInt()
        ).idsList.asFlow()

        val shopResponseList = satisfiedShopIdFlow
            .map { shopQueryService.findShopById(it)!! }
            .map { it.toSimpleResponse() }
            .toList()

        check(shopResponseList.isNotEmpty()) {
            throw ShopNotFoundException("Shop is not found!!")
        }

        return@coroutineScope ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(ResultFactory.getMultipleResult(shopResponseList))
    }
}