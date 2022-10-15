package team.bakkas.servicecommand.validator

import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils

/** 모든 validator에서 공통적으로 사용하는 로직들을 묶어둔 클래스
 * @author Doyeop Kim
 * @since 2022/10/14
 */
open class CommonValidator {

    // 주어진 field가 비어있는지 검증해주는 메소드
    fun rejectFieldIfEmpty(errors: Errors, fieldName: String, message: String) {
        ValidationUtils.rejectIfEmptyOrWhitespace(
            errors,
            fieldName,
            "field.required",
            arrayOf(),
            message
        )
    }

    // 모든 필드를 대상으로 empty 검사를 시행하는 메소드
    fun rejectEmptyByFieldList(errors: Errors, fieldNameList: List<String>) {
        fieldNameList.forEach { rejectFieldIfEmpty(errors, it, "$it is required") }
    }

    // field 검증을 배타적으로 수행하는 메소드 (i.e. N 개중 하나만 들어와도 괜찮은 경우)
    @Suppress("UNCHECKED_CAST")
    fun rejectEmptyFieldExclusively(target: Any, errors: Errors, fieldNameList: List<String>) {
        // get memberProperties by java reflection
        val memberProperties = target::class.java.fields

        // 검증해야하는 field들의 리스트
        val validateFields = memberProperties.filter { it.name in fieldNameList }
            .map { it as String? }

        // 내용물이 채워져있는 String field의 개수
        val satisfyVariableNumber = validateFields
            .filter { !it.isNullOrEmpty() }
            .size

        // 만약에 조건에 부합한 원소가 하나도 없다면 reject 시킨다
        if (satisfyVariableNumber == 0) {
            rejectEmptyByFieldList(errors, fieldNameList)
        }
    }

    /** field에 대해서 값 자체가 잘못되었을 때 error를 검증해주는 메소드
     * @param errors
     * @param fieldName
     * @param errorCode
     * @param value
     * @param message
     */
    fun rejectFieldWithValue(errors: Errors, fieldName: String, errorCode: String, value: String, message: String) {
        errors.rejectValue(fieldName, errorCode, arrayOf(value), message)
    }
}