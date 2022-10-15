package team.bakkas.clientcommand.shop.annotations

/** Shop이 삭제 가능한지 검증하는데 사용되는 어노테이션
 * @author Doyeop Kim
 * @since 2022/10/15
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ShopDeletable(
    val defaultMessage: String = "FieldError: more than one field is empty"
) {
}