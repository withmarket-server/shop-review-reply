package team.bakkas.clientcommand.shopReview.annotations

/** 해당 리뷰가 생성 가능한지 검증하는데 사용되는 어노테이션
 * @author Doyeop Kim
 * @since 2022/10/15
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ReviewCreatable(
    val defaultMessage: String = "FieldError: more than one field is empty"
) {
}