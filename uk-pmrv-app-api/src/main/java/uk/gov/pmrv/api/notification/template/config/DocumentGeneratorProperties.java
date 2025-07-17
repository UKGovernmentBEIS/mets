package uk.gov.pmrv.api.notification.template.config;

import jakarta.validation.Valid;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Validated
@ConfigurationProperties(prefix = "document-generator")
@Data
public class DocumentGeneratorProperties {
	
    private boolean remote;

    @Valid
	@NotEmpty @URL
    private String url;
}
