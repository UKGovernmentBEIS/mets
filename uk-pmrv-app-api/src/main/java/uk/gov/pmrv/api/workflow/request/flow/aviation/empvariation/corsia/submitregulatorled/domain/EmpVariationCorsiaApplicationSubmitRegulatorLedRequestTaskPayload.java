package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain;

import java.util.EnumMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain.EmpCorsiaReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationRequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload
    extends EmpVariationCorsiaApplicationRequestTaskPayload {

    private EmissionsMonitoringPlanCorsiaContainer originalEmpContainer;

    private String reasonRegulatorLed;
    
    @Builder.Default
    private Map<EmpCorsiaReviewGroup, EmpAcceptedVariationDecisionDetails> reviewGroupDecisions =
        new EnumMap<>(EmpCorsiaReviewGroup.class);
}
