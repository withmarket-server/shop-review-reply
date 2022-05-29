package team.bakkas.common.exceptions

data class RequestParamLostException(override var message: String): RuntimeException(message) {

}