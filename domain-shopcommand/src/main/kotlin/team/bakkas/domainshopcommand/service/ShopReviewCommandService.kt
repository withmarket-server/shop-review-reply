package team.bakkas.domainshopcommand.service

import org.springframework.stereotype.Service
import team.bakkas.domaindynamo.repository.ShopReviewDynamoRepository

@Service
class ShopReviewCommandService(
    private val shopReviewDynamoRepository: ShopReviewDynamoRepository
) {


}