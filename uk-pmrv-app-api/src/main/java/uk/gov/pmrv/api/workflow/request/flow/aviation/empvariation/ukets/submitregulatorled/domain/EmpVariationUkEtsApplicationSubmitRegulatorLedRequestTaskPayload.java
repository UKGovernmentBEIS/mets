package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain;

import java.util.EnumMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.domain.EmpUkEtsReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpAcceptedVariationDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsApplicationRequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload extends EmpVariationUkEtsApplicationRequestTaskPayload {

	private EmissionsMonitoringPlanUkEtsContainer originalEmpContainer;
	
	private EmpVariationUkEtsRegulatorLedReason reasonRegulatorLed;
	
	@Builder.Default
    private Map<EmpUkEtsReviewGroup, EmpAcceptedVariationDecisionDetails> reviewGroupDecisions = new EnumMap<>(EmpUkEtsReviewGroup.class);
	
}
