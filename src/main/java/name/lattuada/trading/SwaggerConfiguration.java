package name.lattuada.trading;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .description("Simple Trading application with REST API and Cucumber tests")
                        .title("Trading application")
                        .version("0.0.2")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("name.lattuada.trading.controller"))
                .paths(PathSelectors.any())
                .build();
    }

}
