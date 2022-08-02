package team.bakkas.common.exceptions

data class RequestBodyLostException(override val message: String?): RuntimeException(message) {

}