package team.bakkas.dao.config.dynamo

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

/**
 * DynamoDbConfig(val accessKey: String, secretKey: String)
 * Netty Engine 기반의 비동기 접근을 통해 AsyncClient를 사용하기 위해 정의한 Spring Configuration class
 * @param accessKey DynamoDB의 FullAccess IAM의 액세스 키.
 * @param secretKey DynamoDB의 FullAccess IAM의 시크릿 키.
 */
@Configuration
class DynamoDbConfig(
    @Value("\${aws.dynamodb.credentials.access-key}")
    val accessKey: String,
    @Value("\${aws.dynamodb.credentials.secret-key}")
    val secretKey: String
) {

    /**
     * DynamoDb에 접근하기 위한 metadata를 이용하여 asyncClient를 생성해내는 메소드
     */
    @Bean
    fun dynamoDbAsyncClient(): DynamoDbAsyncClient = DynamoDbAsyncClient.builder()
        .region(Region.AP_NORTHEAST_2)
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            )
        )
        .build()

    /**
     * dynamoDbEnhancedAsyncClient(dynamoDbAsyncClient: DynamoDbAsyncClient)
     * dynamo persist, query를 사용하기 위해 정의하는 asyncClient
     * @param dynamoDbAsyncClient
     */
    @Bean(name = ["dynamoDbEnhancedAsyncClient"])
    fun dynamoDbEnhancedAsyncClient(
        @Qualifier("dynamoDbAsyncClient") dynamoDbAsyncClient: DynamoDbAsyncClient
    ): DynamoDbEnhancedAsyncClient = DynamoDbEnhancedAsyncClient.builder()
        .dynamoDbClient(dynamoDbAsyncClient)
        .build()
}