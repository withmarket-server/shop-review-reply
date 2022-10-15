package team.bakkas.clientcommand.shop.annotations

/** Shop이 생성 가능한지 검증하는데 적용되는 어노테아션
 * @author Doyeop Kim
 * @since 2022/10/15
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ShopCreatable(
    val defaultMessage: String = "FieldError: more than one field is empty"
) {
}