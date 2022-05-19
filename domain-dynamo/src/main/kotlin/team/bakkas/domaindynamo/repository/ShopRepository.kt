package team.bakkas.domaindynamo.repository

import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient

@Repository
class ShopRepository(
    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient,
) {

}