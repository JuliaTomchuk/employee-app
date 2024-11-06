package by.andersen.employee.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi employeeApi() {
        return GroupedOpenApi.builder()
                .group("Employee API")
                .pathsToMatch("/api/v1/employees/**")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                        .title("Employee API")
                        .description("API to manage all employees")
                        .version("v1")))
                .build();
    }

    @Bean
    public GroupedOpenApi workerApi() {
        return GroupedOpenApi.builder()
                .group("Worker API")
                .pathsToMatch("/api/v1/workers/**")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                        .title("Worker API")
                        .description("API to manage workers")
                        .version("v1")))
                .build();
    }

    @Bean
    public GroupedOpenApi managerApi() {
        return GroupedOpenApi.builder()
                .group("Manager API")
                .pathsToMatch("/api/v1/managers/**")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                        .title("Manager API")
                        .description("API to manage managers")
                        .version("v1")))
                .build();
    }

    @Bean
    public GroupedOpenApi otherWorkerApi() {
        return GroupedOpenApi.builder()
                .group("Other worker API")
                .pathsToMatch("/api/v1/other-workers/**")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                        .title("Other worker API")
                        .description("API to manage other workers")
                        .version("v1")))
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearer-key",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT authorization via Keycloak")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
    }
}
