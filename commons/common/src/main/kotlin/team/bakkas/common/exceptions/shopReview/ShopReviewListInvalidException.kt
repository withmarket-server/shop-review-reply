package team.bakkas.common.exceptions.shopReview

/** ShopReviewList를 전달하는 과정에서 list의 정합성이 훼손된 경우 예외를 처리하는 클래스
 * @author Brian
 * @since 22/06/03
 */
class ShopReviewListInvalidException(override val message: String) : RuntimeException(message) {

}
