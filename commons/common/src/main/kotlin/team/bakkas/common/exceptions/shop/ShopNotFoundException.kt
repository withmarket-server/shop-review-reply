package team.bakkas.common.exceptions.shop

/** Shop이 발견되지 않았을 때를 담당하는 에외 클래스
 * @author Brian
 * @since 22/05/29
 */
data class ShopNotFoundException(override val message: String) : RuntimeException(message) {

}