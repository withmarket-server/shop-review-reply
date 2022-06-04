package team.bakkas.common.exceptions

data class ShopReviewNotFoundException(override val message: String): RuntimeException(message) {

}
