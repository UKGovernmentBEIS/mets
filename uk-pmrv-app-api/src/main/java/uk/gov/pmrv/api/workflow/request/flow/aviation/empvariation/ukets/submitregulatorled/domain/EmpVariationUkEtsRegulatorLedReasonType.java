package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmpVariationUkEtsRegulatorLedReasonType {

	FOLLOWING_IMPROVING_REPORT(false),
	FAILED_TO_COMPLY_OR_APPLY(false),
	OTHER(true)
	;
	
	private boolean otherSummaryApplies;
}
