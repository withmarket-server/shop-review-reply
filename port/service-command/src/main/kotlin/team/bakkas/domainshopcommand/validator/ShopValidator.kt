package team.bakkas.domainshopcommand.validator

import org.springframework.validation.Validator
import team.bakkas.dynamo.shop.Shop

interface ShopValidator: Validator {

    // 해당 shop이 생성 가능한지 검증하는 메소드
    fun validateCreatable(shop: Shop)
}