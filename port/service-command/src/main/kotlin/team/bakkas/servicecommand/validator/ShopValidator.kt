package team.bakkas.servicecommand.validator

import org.springframework.validation.Validator
import team.bakkas.clientcommand.shop.ShopCommand

abstract class ShopValidator: Validator, CommonValidator() {

    /** 해당 shop이 생성 가능한지 검증하는 메소드
     * @param createRequest shop 생성 요청 dto
     */
    abstract fun validateCreatable(createRequest: ShopCommand.CreateRequest)

    /** 해당 shop이 수정 가능한지 검증하는 메소드
     * @param updateRequest shop 수정 요청 dto
     */
    abstract suspend fun validateUpdatable(updateRequest: ShopCommand.UpdateRequest)

    abstract suspend fun validateDeletable(shopId: String)
}