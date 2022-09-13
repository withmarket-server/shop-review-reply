package team.bakkas.domainshopcommand.validator

import org.springframework.validation.Validator
import team.bakkas.domaindynamo.entity.Shop

interface ShopValidator: Validator {

    // 해당 shop이 생성 가능한지 검증하는 메소드
    fun validateCreatable(shop: Shop)
}