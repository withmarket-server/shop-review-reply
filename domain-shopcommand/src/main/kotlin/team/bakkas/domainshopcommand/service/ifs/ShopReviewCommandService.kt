package team.bakkas.domainshopcommand.service.ifs

import team.bakkas.clientcommand.dto.ShopReviewCommand
import team.bakkas.domaindynamo.entity.ShopReview

interface ShopReviewCommandService {

    // shop에 대한 review를 생성하는 메소드
    suspend fun createReview(reviewCreateDto: ShopReviewCommand.CreateDto): ShopReview
}