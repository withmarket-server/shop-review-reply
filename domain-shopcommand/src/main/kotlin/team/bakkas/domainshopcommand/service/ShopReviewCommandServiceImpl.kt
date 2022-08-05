package team.bakkas.domainshopcommand.service

import org.springframework.stereotype.Service
import team.bakkas.domaindynamo.repository.dynamo.ShopReviewDynamoRepository
import team.bakkas.domainshopcommand.service.ifs.ShopReviewCommandService

@Service
class ShopReviewCommandServiceImpl(
    private val shopReviewDynamoRepository: ShopReviewDynamoRepository
): ShopReviewCommandService {


}