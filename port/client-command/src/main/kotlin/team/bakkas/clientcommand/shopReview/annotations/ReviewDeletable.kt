package team.bakkas.clientcommand.shopReview.annotations

/** 해당 Review가 삭제 가능한지 검증하는데 사용하는 어노테이션
 * @author Doyeop Kim
 * @since 2022/10/15
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ReviewDeletable(
    val defaultMessage: String = "FieldError: more than one field is empty"
) {
}