package team.bakkas.domaindynamo.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration
class DynamoDbConfig(
    @Value("\${aws.dynamodb.credentials.access-key}")
    val accessKey: String,
    @Value("\${aws.dynamodb.credentials.secret-key}")
    val secretKey: String
) {

    // dynamoDB Client Bean 생성
    @Bean
    fun dynamoDbClient(): DynamoDbClient = DynamoDbClient.builder()
        .region(Region.AP_NORTHEAST_2)
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            )
        )
        .build()

    // dynamoDB Enhanced Client 생성
    @Bean
    fun dynamoDbEnhancedClient(
        @Qualifier("dynamoDbClient") dynamoDbClient: DynamoDbClient
    ): DynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
        .dynamoDbClient(dynamoDbClient)
        .build()
}