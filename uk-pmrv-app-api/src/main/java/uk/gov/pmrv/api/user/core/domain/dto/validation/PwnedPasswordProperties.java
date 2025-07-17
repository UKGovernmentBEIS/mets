package uk.gov.pmrv.api.user.core.domain.dto.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "pwned-password")
@Getter
@Setter
public class PwnedPasswordProperties {
	@Valid
	@NotEmpty @URL
	private String serviceUrl;
}
