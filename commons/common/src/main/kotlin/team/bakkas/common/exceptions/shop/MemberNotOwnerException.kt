package team.bakkas.common.exceptions.shop

// member가 해당 가게의 주인이 아닌 경우 발생되는 exception
class MemberNotOwnerException(override val message: String): RuntimeException(message) {

}
