package team.bakkas.common.exceptions

// Shop이 null일 때 발생하는 Exception
data class ShopNotFoundException(override val message: String): RuntimeException(message) {

}