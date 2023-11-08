package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.EmpUkEtsOriginatedData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayloadVerifiable;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerUkEtsRequestPayload extends AviationAerRequestPayload
    implements RequestPayloadVerifiable<AviationAerUkEtsVerificationReport>  {

    private AviationAerUkEts aer;

    private AviationAerUkEtsVerificationReport verificationReport;

    //Represents the total emissions for scheme year when the operator sent the application to verifier.
    //Needed in order to initialize review task payload with proper data
    private BigDecimal totalEmissionsProvided;

    //Represents the changes provided in the Monitoring Plan Info section (if any) when the operator sent the application to verifier.
    //Needed in order to initialize review task payload with proper data
    private String notCoveredChangesProvided;

    @Builder.Default
    private Map<AviationAerUkEtsReviewGroup, AerReviewDecision> reviewGroupDecisions = new EnumMap<>(AviationAerUkEtsReviewGroup.class);

    private EmpUkEtsOriginatedData empOriginatedData;
    
    @Override
    @JsonIgnore
    public AviationAerVerificationData getVerificationData() {
        return verificationReport == null ? null : verificationReport.getVerificationData();
    }
}
