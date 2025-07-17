package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationSubmittedRequestActionPayload;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload extends
    AviationAerCorsiaApplicationSubmittedRequestActionPayload {

    private BigDecimal totalEmissionsProvided;

    private BigDecimal totalOffsetEmissionsProvided;
}
