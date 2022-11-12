package team.bakkas.eventinterface.eventProducer

import team.bakkas.clientcommand.shop.ShopCommand
import team.bakkas.dynamo.shop.Shop

interface ShopEventProducer {

    // shop이 생성되었을 때의 이벤트를 전파하는 메소드
    fun propagateShopCreated(shop: Shop)

    // shop이 삭제되었을 때의 이벤트를 전파하는 메소드
    fun propagateShopDeleted(deletedEvent: ShopCommand.DeletedEvent)

    // Shop이 수정되었을 때의 이벤트를 전파하는 메소드
    fun propagateShopUpdated(updatedEvent: ShopCommand.UpdateRequest)
}