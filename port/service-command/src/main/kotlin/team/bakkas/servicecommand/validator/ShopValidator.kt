package team.bakkas.servicecommand.validator

import org.springframework.validation.Validator
import team.bakkas.clientcommand.shop.ShopCommand

/**
 * ShopValidator
 * Shop에 대한 validator의 interface
 */
abstract class ShopValidator: Validator, CommonValidator() {

    abstract fun validateCreatable(createRequest: ShopCommand.CreateRequest)

    abstract suspend fun validateUpdatable(updateRequest: ShopCommand.UpdateRequest)

    abstract suspend fun validateDeletable(shopId: String)
}