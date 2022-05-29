package team.bakkas.common.error

/** 해당 shop application에서 발생할 수 있는 모든 에러를 정의한 enum class
 * @param status HTTP 통신코드
 * @param errorCode 사용자 정의 에러코드
 * @param message 에러에 실릴 메시지를 정의한 변수
 */
enum class ErrorCode(
    val status: Int,
    val errorCode: String,
    val message: String
) {
    ACCESS_DENIED(400, "C001", "Access is denied"),
    ENTITY_NOT_FOUND(500, "C101", "Not exist entity")
}