package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "payment.variation-manual")
public class PaymentVariationManualProperties {

    private Set<CompetentAuthorityEnum> cas = new HashSet<>();
}
