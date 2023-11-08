package uk.gov.pmrv.api.web.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Year;

/**
 * Configuration for REST API documentation.
 */
@Configuration
public class SwaggerConfig {
    private final BuildProperties buildProperties;

    public SwaggerConfig(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        SpringDocUtils.getConfig().replaceWithSchema(
                BigDecimal.class,
                new Schema<BigDecimal>().type("string").format("decimal")
        );

        SpringDocUtils.getConfig().replaceWithSchema(
                Year.class,
                new Schema<Year>().type("integer").format("int16")
        );

        return new OpenAPI().info(new Info()
                        .title("PMRV API Documentation")
                        .version(String.format("%s %s", buildProperties.getName(), buildProperties.getVersion()))
                        .description("PMRV API Documentation"))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")));
    }
}