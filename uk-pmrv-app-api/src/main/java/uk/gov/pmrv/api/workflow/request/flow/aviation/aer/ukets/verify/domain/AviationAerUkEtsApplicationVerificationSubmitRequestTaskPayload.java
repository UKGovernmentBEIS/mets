package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.domain.AviationAerUkEtsApplicationRequestTaskPayload;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload extends AviationAerUkEtsApplicationRequestTaskPayload {

    private AviationAerUkEtsVerificationReport verificationReport;

    private BigDecimal totalEmissionsProvided;

    private String notCoveredChangesProvided;
}
