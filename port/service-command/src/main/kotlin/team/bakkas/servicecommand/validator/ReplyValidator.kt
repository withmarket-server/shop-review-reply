package team.bakkas.servicecommand.validator

import org.springframework.validation.Validator
import team.bakkas.clientcommand.reply.ReplyCommand

/**
 * ReplyValidator
 * Reply에 대한 validator의 interface
 */
abstract class ReplyValidator: Validator, CommonValidator() {

    // 해당 답글이 생성 가능한지 검증하는 메소드
    abstract suspend fun validateCreatable(request: ReplyCommand.CreateRequest)

    abstract suspend fun validateDeletable(request: ReplyCommand.DeleteRequest)
}