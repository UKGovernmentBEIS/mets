package uk.gov.pmrv.api.aviationreporting.ukets.domain.verification;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.verificationbody.domain.verificationreport.VerificationReport;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class AviationAerUkEtsVerificationReport extends VerificationReport {

    @NotNull
    private Boolean safExists; //TODO remove me in favor of the source aer's safExists

    @JsonUnwrapped
    @Valid
    private AviationAerUkEtsVerificationData verificationData;
}
