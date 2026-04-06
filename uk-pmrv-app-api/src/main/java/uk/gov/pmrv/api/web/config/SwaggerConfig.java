package uk.gov.pmrv.api.web.config;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Map;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import uk.gov.netz.api.mireport.system.EmptyMiReportSystemParams;
import uk.gov.netz.api.mireport.system.accountsregulatorsitecontacts.AccountAssignedRegulatorSiteContactsMiReportResult;
import uk.gov.netz.api.mireport.system.accountuserscontacts.AccountsUsersContactsMiReportResult;
import uk.gov.netz.api.mireport.system.executedactions.ExecutedRequestActionsMiReportParams;
import uk.gov.netz.api.mireport.system.executedactions.ExecutedRequestActionsMiReportResult;
import uk.gov.netz.api.mireport.system.outstandingrequesttasks.OutstandingRegulatorRequestTasksMiReportParams;
import uk.gov.netz.api.mireport.system.outstandingrequesttasks.OutstandingRequestTasksMiReportResult;
import uk.gov.pmrv.api.mireport.system.common.verificationbodyusers.VerificationBodyUsersMiReportResult;

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
                        .title("METS API Documentation")
                        .version(String.format("%s %s", buildProperties.getName(), buildProperties.getVersion()))
                        .description("METS API Documentation"))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")));
    }
    
    @Bean
    OpenApiCustomizer additionalSchemas() {
        return openApi -> {
            Map<String, Schema> schemas = openApi.getComponents().getSchemas();

            List.of(
            	// mi report system results
                AccountAssignedRegulatorSiteContactsMiReportResult.class,
                AccountsUsersContactsMiReportResult.class,
                ExecutedRequestActionsMiReportResult.class,
                OutstandingRequestTasksMiReportResult.class,
                VerificationBodyUsersMiReportResult.class,
                
                // mi report system params
                EmptyMiReportSystemParams.class,
                ExecutedRequestActionsMiReportParams.class,
                OutstandingRegulatorRequestTasksMiReportParams.class
            ).forEach(clazz ->
                schemas.putAll(ModelConverters.getInstance().readAll(clazz))
            );
        };
    }
}