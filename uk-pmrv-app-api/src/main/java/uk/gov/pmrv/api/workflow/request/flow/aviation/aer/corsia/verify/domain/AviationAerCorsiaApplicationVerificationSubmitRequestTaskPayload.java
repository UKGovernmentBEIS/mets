package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationRequestTaskPayload;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AviationAerCorsiaApplicationVerificationSubmitRequestTaskPayload
        extends AviationAerCorsiaApplicationRequestTaskPayload {

    private AviationAerCorsiaVerificationReport verificationReport;

    private BigDecimal totalEmissionsProvided;

    private BigDecimal totalOffsetEmissionsProvided;
}
