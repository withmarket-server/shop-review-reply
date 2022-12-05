package team.bakkas.servicecommand.validator

import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils
import team.bakkas.common.error.ErrorResponse
import team.bakkas.common.exceptions.RequestFieldException

/**
 * CommonValidator
 * 모든 validator에서 공통적으로 사용하는 로직들을 묶어둔 클래스
 */
open class CommonValidator {

    /**
     * rejectEmptyField(errors: Errors, fieldName: String, message: String)
     * 주어진 fieldName에 대응하는 파라미터가 empty인 경우 errors에 reject를 남겨주는 메소드
     * @param errors error를 담는 객체
     * @param fieldName validate를 원하는 field의 이름
     * @param message error message
     */
    private fun rejectEmptyField(errors: Errors, fieldName: String, message: String) {
        ValidationUtils.rejectIfEmptyOrWhitespace(
            errors,
            fieldName,
            "field.required",
            arrayOf(),
            message
        )
    }

    /**
     * rejectEmptyFieldList(errors: Errors, fieldNameList: List<String>)
     * 주어진 fieldList에 대응하는 파라미터들에 대하여 empty 검증을 수행해주는 메소드
     * @param errors error를 담는 객체
     * @param fieldNameList fieldName을 모아둔 List 객체
     */
    fun rejectEmptyFieldList(errors: Errors, fieldNameList: List<String>) {
        fieldNameList.forEach { rejectEmptyField(errors, it, "$it is required") }
    }

    /**
     * rejectFieldsExclusively(target: Any, errors: Errors, fieldNameList: List<String>)
     * fieldNameList에 존재하는 파라미터들에 대해서 배타적인 검증을 수행하는 메소드
     * 즉, fieldNameList에 대해서 1개 이상이 empty가 아니면 검증을 통과시키는 기능을 수행한다.
     * @param target 검증 타겟
     * @param errors error를 저장하는 객체
     * @param fieldNameList 배타 검증을 수행하고자하는 fieldName의 List 객체
     */
    @Suppress("UNCHECKED_CAST")
    fun rejectFieldsExclusively(target: Any, errors: Errors, fieldNameList: List<String>) {
        val memberProperties = target::class.java.fields

        val targetFields = memberProperties
            .filter { it.name in fieldNameList }
            .map { it as String? }

        val satisfyVariableNumber = targetFields
            .filter { !it.isNullOrEmpty() }
            .size

        if (satisfyVariableNumber == 0) {
            rejectEmptyFieldList(errors, fieldNameList)
        }
    }

    /**
     * rejectFieldWithValue(errors: Errors, fieldName: String, errorCode: String, value: String, message: String)
     * 검증 field에 대해서 잘못된 값을 검증해내는 기능을 수행하는 메소드
     * ex) 0~10 사이의 평점이 허용된 리뷰에 대해서 11점이 들어온 경우 reject를 일으킨다
     * ex) 0~10 사이의 평점이 허용된 리뷰에 대해서 음수의 평점이 들어온 경우 reject를 일으킨다
     * @param errorCode 일으키고자하는 error의 코드
     * @param value 잘못 전달된 field의 값
     */
    fun rejectFieldWithValue(errors: Errors, fieldName: String, errorCode: String, value: String, message: String) {
        errors.rejectValue(fieldName, errorCode, arrayOf(value), message)
    }

    /**
     * throwsIfErrorExists(errors: Errors)
     * errors에 기록된 에러가 하나 이상일 경우 예외를 발생시키는 메소드
     * @param errors 에러의 집합 객체
     * @throws RequestFieldException
     */
    fun throwsExceptionIfErrorExists(errors: Errors) {
        // field error들을 모두 취합하여 exception을 던진다
        check(errors.allErrors.isEmpty()) {
            val errorList = errors.allErrors.map {
                ErrorResponse.FieldError.of(
                    it.objectName,
                    it.arguments.contentToString(),
                    it.defaultMessage!!
                )
            }
            throw RequestFieldException(errorList, "잘못된 요청입니다")
        }
    }
}