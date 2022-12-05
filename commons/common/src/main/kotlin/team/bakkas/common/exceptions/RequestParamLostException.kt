package team.bakkas.common.exceptions

/** Request Parameter가 소실된 경우를 담당하는 예외 class
 * @param message
 * @author Brian
 * @since 22/05/29
 */
class RequestParamLostException(override var message: String) : RuntimeException(message) {

}