package team.bakkas.eventinterface.eventProducer

import team.bakkas.clientcommand.shop.ShopCommand
import team.bakkas.dynamo.shop.Shop

/**
 * ShopEventProducer
 * Shop에 대한 이벤트의 발행을 담당하는 interface
 */
interface ShopEventProducer {

    fun propagateShopCreated(shop: Shop)

    fun propagateShopDeleted(deletedEvent: ShopCommand.DeletedEvent)

    fun propagateShopUpdated(updatedEvent: ShopCommand.UpdateRequest)
}