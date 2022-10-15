package team.bakkas.applicationcommand.handler

import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import team.bakkas.applicationcommand.grpc.ifs.ShopGrpcClient
import team.bakkas.applicationcommand.grpc.ifs.ShopReviewGrpcClient
import team.bakkas.applicationcommand.validator.ShopReviewValidatorImpl
import team.bakkas.eventinterface.eventProducer.ShopReviewEventProducer
import team.bakkas.servicecommand.validator.ShopReviewValidator

@ExtendWith(MockKExtension::class)
internal class ShopReviewCommandHandlerUnitTest {
    private lateinit var shopReviewCommandHandler: ShopReviewCommandHandler

    private lateinit var shopReviewValidator: ShopReviewValidator

    private lateinit var shopGrpcClient: ShopGrpcClient

    private lateinit var shopReviewGrpcClient: ShopReviewGrpcClient

    private lateinit var shopReviewEventProducer: ShopReviewEventProducer

    @BeforeEach
    fun setUp() {
        shopGrpcClient = mockk(relaxed = true)
        shopReviewGrpcClient = mockk(relaxed = true)
        shopReviewValidator = spyk(ShopReviewValidatorImpl(shopGrpcClient, shopReviewGrpcClient))
        shopReviewEventProducer = mockk(relaxed = true)
        shopReviewCommandHandler = spyk(ShopReviewCommandHandler(shopReviewValidator, shopReviewEventProducer))
    }


}