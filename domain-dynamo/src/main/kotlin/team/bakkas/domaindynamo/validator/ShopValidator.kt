package team.bakkas.domaindynamo.validator

import org.springframework.stereotype.Component
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils
import org.springframework.validation.Validator
import team.bakkas.common.exceptions.RegionNotKoreaException
import team.bakkas.common.exceptions.RequestFieldException
import team.bakkas.common.exceptions.ShopBranchInfoInvalidException
import team.bakkas.domaindynamo.entity.Shop

// Shop을 검증하는 로직을 정의하는 클래스
@Component
class ShopValidator: Validator {

    override fun supports(clazz: Class<*>): Boolean {
        return Shop::class.java.isAssignableFrom(clazz)
    }

    override fun validate(target: Any, errors: Errors) {
        // shopName, lotNumberAddress, roadNameAddress에 대한 필드 유효성 검증
        ValidationUtils.rejectIfEmpty(errors, "shopName", "field.required", "shopName이 비어있습니다.")
        ValidationUtils.rejectIfEmpty(errors, "lotNumberAddress", "field.required", "지번주소가 제공되지 않았습니다.")
        ValidationUtils.rejectIfEmpty(errors, "roadNameAddress", "field.required", "도로명 주소가 제공되지 않았습니다.")

        // TODO 패턴을 분석하길 원하면 Pattern.compile을 이용해 정규표현식으로 패턴을 구현하고, errors.rejectValue를 이용해 에러 때린다
    }

    // 해당 가게가 생성 가능한지 검증하는 메소드
    fun validateCreatable(shop: Shop) = with(shop) {
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

        check(errors == null || errors.allErrors.isEmpty()) {
            throw RequestFieldException(errors.allErrors.toString())
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
        val firstCondition = (isBranch && !branchName.isNullOrEmpty())
        val secondCondition = (!isBranch && branchName.isNullOrEmpty())

        return firstCondition || secondCondition
    }
}