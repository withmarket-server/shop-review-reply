package team.bakkas.servicecommand.validator

import org.springframework.validation.Validator
import team.bakkas.clientcommand.shop.ShopCommand

abstract class ShopValidator: Validator, CommonValidator() {

    // 해당 shop이 생성 가능한지 검증하는 메소드
    abstract fun validateCreatable(createRequest: ShopCommand.CreateRequest)
}