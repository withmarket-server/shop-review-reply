package team.bakkas.applicationcommand.validator

import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils
import team.bakkas.common.error.ErrorResponse
import team.bakkas.common.exceptions.RegionNotKoreaException
import team.bakkas.common.exceptions.RequestFieldException
import team.bakkas.common.exceptions.shop.ShopBranchInfoInvalidException
import team.bakkas.dynamo.shop.Shop
import team.bakkas.domainshopcommand.validator.ShopValidator

// Shop을 검증하는 로직을 정의하는 클래스
@Component
class ShopValidatorImpl : ShopValidator {

    override fun supports(clazz: Class<*>): Boolean {
        return Shop::class.java.isAssignableFrom(clazz)
    }

    override fun validate(target: Any, errors: Errors) {
        // shopName, lotNumberAddress, roadNameAddress에 대한 필드 유효성 검증
        ValidationUtils.rejectIfEmpty(errors, "shopName", "field.required", arrayOf(), "shopName이 비어있습니다.")
        ValidationUtils.rejectIfEmpty(errors, "lotNumberAddress", "field.required", arrayOf(), "지번주소가 제공되지 않았습니다.")
        ValidationUtils.rejectIfEmpty(errors, "roadNameAddress", "field.required", arrayOf(), "도로명 주소가 제공되지 않았습니다.")
    }

    // 해당 가게가 생성 가능한지 검증하는 메소드
    override fun validateCreatable(shop: Shop) = with(shop) {
        validateFirst(this)

        check(validateIsInSouthKorea(latitude, longitude)) {
            throw RegionNotKoreaException("주어진 좌표가 한국(South Korea)내에 존재하지 않습니다.")
        }

        check(validateBranchInfo(isBranch, branchName)) {
            throw ShopBranchInfoInvalidException("본점/지점 정보가 잘못 주어졌습니다.")
        }
    }

    // 제일 먼저 필드의 유효성을 검증하는 메소드
    private fun validateFirst(shop: Shop) = with(shop) {
        val errors = BeanPropertyBindingResult(this, Shop::class.java.name)
        validate(this, errors)

        check(errors.allErrors.isEmpty()) {
            val errorList = errors.allErrors.map {
                ErrorResponse.FieldError.of(it.objectName, it.arguments.contentToString(), it.defaultMessage!!)
            }
            throw RequestFieldException(errorList, "잘못된 요청입니다.")
        }
    }

    // 해당 가게가 한국에 존재하는 가게인지 검증하는 메소드
    private fun validateIsInSouthKorea(latitude: Double, longitude: Double): Boolean {
        val latitudeSatisfied = latitude > 125.06666667 && latitude < 131.87222222
        val longitudeSatisfied = longitude > 33.10000000 && longitude < 38.45000000

        return latitudeSatisfied && longitudeSatisfied
    }

    // 해당 가게의 지점 정보가 올바른지 검증하는 메소드
    private fun validateBranchInfo(isBranch: Boolean, branchName: String?): Boolean {
        // 1. 본점인데 branchName이 있는 경우
        val firstCondition = (isBranch && !branchName.isNullOrEmpty())
        // 2. 분점인데 branchName이 없는 경우
        val secondCondition = (!isBranch && branchName.isNullOrEmpty())

        return firstCondition || secondCondition
    }
}