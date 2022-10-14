package team.bakkas.dao.config

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
 * netty 기반의 DynamoDB Enhanced Client를 Configuration으로 등록
 * Config Server로부터 access-key, secret-key를 주입받아서 DynamoDBEnhancedClient를 Bean으로 등록한다
 * @since 22/05/18
 * @author Brian
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

    @Bean
    fun dynamoDbAsyncClient(): DynamoDbAsyncClient = DynamoDbAsyncClient.builder()
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

    // Async가 적용된 DynamoDbEnhancedAsyncClient 생성
    @Bean(name = ["dynamoDbEnhancedAsyncClient"])
    fun dynamoDbEnhancedAsyncClient(
        @Qualifier("dynamoDbAsyncClient") dynamoDbAsyncClient: DynamoDbAsyncClient
    ): DynamoDbEnhancedAsyncClient = DynamoDbEnhancedAsyncClient.builder()
        .dynamoDbClient(dynamoDbAsyncClient)
        .build()
}