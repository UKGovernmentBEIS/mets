package uk.gov.pmrv.api.workflow.payment.config.property;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "govuk-pay")
@Data
public class GovukPayProperties {

    @Valid
    @NotBlank
    private String serviceUrl;

    @Valid
    @NotEmpty
    private Map<String, @NotBlank String> apiKeys = new HashMap<>();
}
