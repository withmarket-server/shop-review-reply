package team.bakkas.domainshopcommand.service

import org.springframework.stereotype.Service
import team.bakkas.clientcommand.dto.ShopReviewCommand
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domaindynamo.repository.dynamo.ShopReviewDynamoRepository
import team.bakkas.domainshopcommand.service.ifs.ShopReviewCommandService

@Service
class ShopReviewCommandServiceImpl(
    private val shopReviewDynamoRepository: ShopReviewDynamoRepository
): ShopReviewCommandService {

    override suspend fun createReview(reviewCreateDto: ShopReviewCommand.CreateDto): ShopReview {
        TODO("Not yet implemented")
    }
}