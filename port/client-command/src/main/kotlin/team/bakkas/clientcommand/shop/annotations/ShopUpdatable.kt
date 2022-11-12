package team.bakkas.clientcommand.shop.annotations

/** 가게 수정을 검증하는데 사용하는 어노테이션
 * @author Doyeop Kim
 * @since 2022/11/12
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ShopUpdatable(
    val defaultMessage: String = "FieldError: more than one field is empty"
) {
}