package dev.quochung2003.reactiveLearning.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /**
     * The <b>OpenAPI Specification</b> version you want to use.
     * @see <a href="https://github.com/OAI/OpenAPI-Specification/tree/main/versions">OpenAPI Specification (OAS) Version</a>
     */
    private static final String OPENAPI_VERSION = "3.1.1";

    /**
     * The name about your current API.
     */
    private static final String TITLE = "Reactive Learning";

    /**
     * The version of the current API.
     */
    private static final String API_VERSION = "1.0";

    @Bean
    public OpenAPI customizeOpenAPI() {
        return new OpenAPI()
                .openapi(OPENAPI_VERSION)
                .info(new Info().title(TITLE).version(API_VERSION))
                .components(new Components().addSecuritySchemes(
                        "Bearer Token",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .bearerFormat("JWT")
                                .scheme("bearer")
                ));
    }
}
