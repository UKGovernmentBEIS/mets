package uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DreDeterminationReasonType {

	VERIFIED_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER("Verified report not submitted in accordance with the order", false),
	CORRECTING_NON_MATERIAL_MISSTATEMENT("Correcting a non-material misstatement", false),
	SURRENDER_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER("Surrender report not submitted in accordance with the order", false),
	REVOCATION_REPORT_NOT_SUBMITTED_IN_ACCORDANCE_WITH_ORDER("Revocation report not submitted in accordance with the order", false),
	OTHER(null, true)
	;
	
	private String description;
	private boolean otherReasonApplies;
}
