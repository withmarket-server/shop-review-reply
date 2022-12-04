package team.bakkas.clientcommand.reply.annotations


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ReplyCreatable(
    val defaultMessage: String = "FieldError: more than one field is empty"
)
