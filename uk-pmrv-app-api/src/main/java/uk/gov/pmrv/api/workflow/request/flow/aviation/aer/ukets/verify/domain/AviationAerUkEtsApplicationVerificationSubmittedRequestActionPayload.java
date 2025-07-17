package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsApplicationSubmittedRequestActionPayload;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload extends
    AviationAerUkEtsApplicationSubmittedRequestActionPayload {

    private BigDecimal totalEmissionsProvided;

    private String notCoveredChangesProvided;
}
