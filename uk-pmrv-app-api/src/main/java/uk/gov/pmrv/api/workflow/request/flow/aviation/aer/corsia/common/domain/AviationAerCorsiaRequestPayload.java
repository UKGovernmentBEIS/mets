package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.EmpCorsiaOriginatedData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayloadVerifiable;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerCorsiaRequestPayload extends AviationAerRequestPayload
    implements RequestPayloadVerifiable<AviationAerCorsiaVerificationReport> {

    private AviationAerCorsia aer;

    private AviationAerCorsiaVerificationReport verificationReport;

    private EmpCorsiaOriginatedData empOriginatedData;

    private BigDecimal totalEmissionsProvided;

    private BigDecimal totalOffsetEmissionsProvided;

    private AviationAerCorsiaSubmittedEmissions submittedEmissions;

    @Builder.Default
    private Map<AviationAerCorsiaReviewGroup, AerReviewDecision> reviewGroupDecisions =
        new EnumMap<>(AviationAerCorsiaReviewGroup.class);

    @Override
    @JsonIgnore
    public AviationAerCorsiaVerificationData getVerificationData() {
        return verificationReport == null ? null : verificationReport.getVerificationData();
    }
}
