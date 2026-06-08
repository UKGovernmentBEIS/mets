package uk.gov.pmrv.api.verificationbody.domain.verificationreport;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class VerificationReport {

    private Long verificationBodyId;

    @Valid
    private VerificationBodyDetails verificationBodyDetails;
}
