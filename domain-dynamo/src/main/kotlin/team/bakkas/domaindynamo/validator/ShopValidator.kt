package team.bakkas.domaindynamo.validator

import org.springframework.stereotype.Component
import team.bakkas.common.exceptions.RegionNotKoreaException
import team.bakkas.common.exceptions.ShopBranchInfoInvalidException
import team.bakkas.domaindynamo.entity.Shop

// Shop을 검증하는 로직을 절차지향적으로 정의하는 클래스
@Component
class ShopValidator {

    // 해당 가게가 생성 가능한지 검증하는 메소드
    fun validateCreatable(shop: Shop) = with(shop) {
        check(validateIsInSouthKorea(latitude, longitude)) {
            throw RegionNotKoreaException("주어진 좌표가 한국(South Korea)내에 존재하지 않습니다.")
        }

        check(validateBranchInfo(isBranch, branchName)) {
            throw ShopBranchInfoInvalidException("본점/지점 정보가 잘못 주어졌습니다.")
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