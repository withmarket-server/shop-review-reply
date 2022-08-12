package team.bakkas.domainshopcommand.service

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.bakkas.clientcommand.dto.ShopReviewCommand
import team.bakkas.domaindynamo.entity.ShopReview
import team.bakkas.domaindynamo.repository.dynamo.ShopReviewDynamoRepository
import team.bakkas.domaindynamo.validator.ifs.ShopReviewValidator
import team.bakkas.domainshopcommand.extensions.toEntity
import team.bakkas.domainshopcommand.service.ifs.ShopReviewCommandService

@Service
class ShopReviewCommandServiceImpl(
    private val shopReviewDynamoRepository: ShopReviewDynamoRepository,
    private val shopReviewValidator: ShopReviewValidator
) : ShopReviewCommandService {

    @Transactional
    override suspend fun createReview(reviewCreateDto: ShopReviewCommand.CreateDto): ShopReview {
        val review = reviewCreateDto.toEntity()
        // 검증
        shopReviewValidator.validateCreatable(review)

        // 검증이 끝나면 review 생성
        shopReviewDynamoRepository.createReviewAsync(review).awaitSingle()

        return review
    }
}