package team.bakkas.applicationquery.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

/** REST API 문서 자동화를 위한 Swagger config class
 * @author Brian
 * @since 22/06/04
 */
@Configuration
@EnableSwagger2
class SwaggerConfig {

    private val API_NAME = "WithMarket Shop Server Application"
    private val API_VERSION = "1.0.0"
    private val API_DESCRIPTION = "WithMarket Shop server application only for query"

    @Bean
    fun api(): Docket = Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build()

    @Bean
    fun apiInfo(): ApiInfo = ApiInfoBuilder()
        .title(API_NAME)
        .description(API_DESCRIPTION)
        .version(API_VERSION)
        .contact(Contact("Brian", "https://velog.io/@18k7102dy", "18k7102dy@naver.com"))
        .build()
}