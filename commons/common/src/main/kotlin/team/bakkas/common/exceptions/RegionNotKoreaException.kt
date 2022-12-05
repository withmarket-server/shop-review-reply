package team.bakkas.common.exceptions

/** 지역이 한국이 아닌 경우에 대해서 예외 처리를 수행해주는 클래스
 * @param message 에러 메시지
 */
class RegionNotKoreaException(override val message: String): RuntimeException(message) {

}