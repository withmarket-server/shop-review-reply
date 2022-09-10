package team.bakkas.common.exceptions

import team.bakkas.common.error.ErrorResponse


// Request로 들어온 객체의 유효성이 해쳐지면 벌어지는 예외 클래스
class RequestFieldException(val errors: List<ErrorResponse.FieldError>): RuntimeException() {

}