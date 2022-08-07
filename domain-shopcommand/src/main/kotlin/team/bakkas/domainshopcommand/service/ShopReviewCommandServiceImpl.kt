package team.bakkas.domainshopcommand.service

import org.springframework.stereotype.Service
import team.bakkas.domaindynamo.repository.dynamo.ShopReviewDynamoRepository

@Service
class ShopReviewCommandServiceImpl(
    private val shopReviewDynamoRepository: ShopReviewDynamoRepository
) {


}