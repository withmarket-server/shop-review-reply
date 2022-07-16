package team.bakkas.domainqueryservice.service

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import team.bakkas.clientmobilequery.dto.ShopReviewBasicReadDto
import team.bakkas.common.ResultFactory
import team.bakkas.common.Results
import team.bakkas.common.exceptions.RequestParamLostException
import team.bakkas.common.exceptions.ShopReviewListInvalidException
import team.bakkas.common.exceptions.ShopReviewNotFoundException
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domaindynamo.repository.ShopReviewDynamoRepository

/** ShopReview의 Query에 대응하는 service code
 * @param shopReviewRepository review를 담당하는 repository bean
 * @author Brian
 * @since 22/06/03
 */
@Service
class ShopReviewQueryService(
    private val shopReviewRepository: ShopReviewDynamoRepository,
    private val resultFactory: ResultFactory
) {

    /** reviewId와 reviewTitle을 키로 가지는 데이터를 가져오는 메소드
     * @param reviewId review의 partition key
     * @param reviewTitle review의 sort key
     * @throws RequestParamLostException
     * @throws ShopReviewNotFoundException
     */
    fun findReviewByIdAndName(
        reviewId: String?,
        reviewTitle: String?
    ): ResponseEntity<Results.SingleResult<ShopReviewBasicReadDto>> {
        // key 조건중 적어도 하나가 유실된 경우
        if (reviewId == null || reviewTitle == null || reviewId.isEmpty() || reviewTitle.isEmpty())
            throw RequestParamLostException("잘못된 형식의 검색. reviewId 혹은 reviewTitle을 확인하십시오.")

        // null이 아닌 경우 변수를 저장하고, null인 경우 바로 예외를 던져준다
        val foundReview = shopReviewRepository.findReviewByIdAndTitle(reviewId, reviewTitle)
            ?: throw ShopReviewNotFoundException("(reviewId = $reviewId, reviewTitle = $reviewTitle)에 해당하는 review는 존재하지 않습니다.")

        return ResponseEntity.ok(resultFactory.getSingleResult(toBasicReadDto(foundReview)))
    }

    /** Shop GSI 정보를 이용해서 대응되는 리뷰들을 모두 가져오는 메소드
     * @param shopId shopReview GSI의 partition key
     * @param shopName shopReview GSI의 sort key
     * @throws RequestParamLostException
     * @throws ShopReviewNotFoundException
     * @throws ShopReviewListInvalidException
     */
    fun getShopReviewListByShopKey(
        shopId: String?,
        shopName: String?
    ): ResponseEntity<Results.MultipleResult<ShopReviewBasicReadDto>> {
        // shopId, shopName 검증
        if (shopId == null || shopName == null || shopId.isEmpty() || shopName.isEmpty())
            throw RequestParamLostException("잘못된 형식의 검색. shopId 혹은 shopName을 확인하십시오.")

        // repository를 이용해서 list를 가져온다. 이 때 리스트의 크기가 0이 아닌지 검증한다
        val reviewList = shopReviewRepository.getReviewListByShopGsi(shopId, shopName)

        // shopReviewList에 대한 예외 처리 -> review를 하나도 못 찾은 경우
        if (reviewList.isEmpty())
            throw ShopReviewNotFoundException("(shopId = $shopId, shopName = $shopName)에 해당하는 review는 존재하지 않습니다.")

        // repository를 이용해서 가져온 list를 모두 검증한다 -> map을 돌면서 shopId, shopName을 검증하고 list로 집어넣는다
        val responseList = reviewList.map { review ->
            if (!review.shopId.equals(shopId) || !review.shopName.equals(shopName))
                throw ShopReviewListInvalidException("review 목록을 가져오는데 문제가 발생하였습니다.")

            toBasicReadDto(review)
        }.toList()

        // 결과를 반환한다.
        return ResponseEntity.ok(resultFactory.getMultipleResult(responseList))
    }

    private fun toBasicReadDto(shopReview: ShopReview) = ShopReviewBasicReadDto(
        reviewId = shopReview.reviewId,
        reviewTitle = shopReview.reviewTitle,
        shopId = shopReview.shopId,
        shopName = shopReview.shopName,
        reviewContent = shopReview.reviewContent,
        reviewScore = shopReview.reviewScore,
        reviewPhotoList = shopReview.reviewPhotoList,
        createdAt = shopReview.createdAt,
        updatedAt = shopReview.updatedAt
    )
}