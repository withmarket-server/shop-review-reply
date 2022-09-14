package team.bakkas.eventinterface.eventProducer

import team.bakkas.domaindynamo.entity.Shop

interface ShopEventProducer {

    // shop이 생성되었을 때의 이벤트를 전파하는 메소드
    fun propagateShopCreated(shop: Shop): Unit
}