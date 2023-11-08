package uk.gov.pmrv.api.aviationreporting.corsia.domain.verification;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.verificationbody.domain.verificationreport.VerificationReport;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class AviationAerCorsiaVerificationReport extends VerificationReport {

    @JsonUnwrapped
    @Valid
    private AviationAerCorsiaVerificationData verificationData;
}
