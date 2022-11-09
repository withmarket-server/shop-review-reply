package team.bakkas.common.exceptions.shop

/**
 * @author Brian
 * @since 2022/11/09
 */
class DetailCategoryNotFoundException(override val message: String) : RuntimeException(message) {
}