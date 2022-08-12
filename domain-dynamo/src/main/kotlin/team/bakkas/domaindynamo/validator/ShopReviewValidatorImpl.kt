package team.bakkas.domaindynamo.validator

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import team.bakkas.common.Results
import team.bakkas.common.utils.WebClientHelper
import team.bakkas.common.exceptions.RequestFieldException
import team.bakkas.common.exceptions.shop.ShopNotFoundException
import team.bakkas.common.exceptions.shopReview.ShopReviewNotFoundException
import team.bakkas.common.urls.ServerUrlsInterface
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domaindynamo.validator.ifs.ShopReviewValidator

/** Shop Review에 대한 검증을 수행하는 Validator class
 * @param urlComponent 활성화된 환경에 따라 url을 변동적으로 관리해주는 bean class. local/server 환경을 분리해서 관리한다
 */
@Component
class ShopReviewValidatorImpl(
    private val urlComponent: ServerUrlsInterface
) : ShopReviewValidator {

    private val baseUrl = urlComponent.SHOP_QUERY_SERVER_URL

    private val shopWebClient = WebClient
        .builder()
        .uriBuilderFactory(WebClientHelper.uriBuilderFactory(baseUrl))
        .baseUrl(baseUrl)
        .build()

    // 해당 리뷰가 생성 가능한지 검증하는 메소드
    override suspend fun validateCreatable(shopReview: ShopReview) = with(shopReview) {
        validateFirst(this) // 우선 필드를 모두 검증한다

        // WebClient를 이용해서 해당 shop이 존재하는지 여부만 뽑아온다
        val shopResultMono: Mono<Boolean> = isExistsShop(shopId, shopName)

        // shop이 존재하지 않는 경우 예외를 발생시킨다
        check(shopResultMono.awaitSingle()) {
            throw ShopNotFoundException("shop review에 대응하는 shop이 존재하지 않습니다.")
        }
    }

    // 해당 review가 삭제 가능한지 검증하는 메소드
    override suspend fun validateDeletable(reviewId: String, reviewTitle: String) {
        // 해당 리뷰가 실제 존재하는건지는 체크해본다
        val reviewResultMono = isExistsReview(reviewId, reviewTitle)

        check(reviewResultMono.awaitSingle()) {
            throw ShopReviewNotFoundException("shop review가 존재하지 않습니다.")
        }
    }

    override fun supports(clazz: Class<*>): Boolean {
        return ShopReview::class.java.isAssignableFrom(clazz)
    }

    // reviewId, reviewTitle, shopId, shopName, reviewContent : 비어있는지 검증
    // reviewContent는 200자 이상으로는 못 쓰도록 검증한다
    override fun validate(target: Any, errors: Errors) {
        // reviewId, reviewTitle, shopId, shopName, reviewContent : 비어있는지 검증
        listOf("reviewId", "reviewTitle", "shopId", "shopName", "reviewContent").forEach { fieldName ->
            ValidationUtils.rejectIfEmpty(errors, fieldName, "field.required", "${fieldName}이 제공되지 않았습니다.")
        }

        val review = target as ShopReview

        // reviewContent의 길이를 200으로 제한한다
        if (review.reviewContent.length > 200) {
            errors.rejectValue("reviewContent", "field.max.length", "review의 내용은 200을 넘어서는 안됩니다.")
        }

        // reivewScore가 0점인 경우 제한한다
        if (review.reviewScore <= 0 || review.reviewScore > 10) {
            errors.rejectValue("reviewScore", "field.value.range", "review score은 무조건 0 초과 10 이하입니다.")
        }
    }

    // 기본적으로 검증해야하는 메소드
    private fun validateFirst(shopReview: ShopReview) = with(shopReview) {
        val errors = BeanPropertyBindingResult(this, ShopReview::class.java.name)
        validate(this, errors)

        // 기본 조건들을 만족하지 못하면 exception을 터뜨린다
        check(errors.allErrors.isEmpty()) {
            throw RequestFieldException(errors.allErrors.toString())
        }
    }

    // 해당 shop이 존재하는지 검증하는 메소드
    private fun isExistsShop(shopId: String, shopName: String): Mono<Boolean> {
        return shopWebClient.get()
            .uri(
                UriComponentsBuilder
                    .fromUriString(urlComponent.SHOP_QUERY_URL)
                    .queryParam("id", shopId)
                    .queryParam("name", shopName)
                    .toUriString()
            ).retrieve()
            .bodyToMono(Results.SingleResult::class.java)
            .doOnError {
                throw ShopNotFoundException("Shop is not found!!")
            }
            .map { result -> result.success }
    }

    // 해당 shopReview가 존재하는지 검증하는 메소드
    private fun isExistsReview(reviewId: String, reviewTitle: String): Mono<Boolean> {
        return shopWebClient.get()
            .uri(
                UriComponentsBuilder
                    .fromUriString(urlComponent.SHOP_REVIEW_URL)
                    .queryParam("id", reviewId)
                    .queryParam("title", reviewTitle)
                    .toUriString()
            ).retrieve()
            .bodyToMono(Results.SingleResult::class.java)
            .doOnError { throw ShopReviewNotFoundException("Shop review is not found!!") }
            .map { it.success }
    }
}