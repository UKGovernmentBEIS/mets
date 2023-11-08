package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermitVariationReasonTemplate {

	WHERE_OPERATOR_FAILED_TO_APPLY_IN_ACCORDANCE_WITH_CONDITIONS(false),
	FOLLOWING_IMPROVEMENT_REPORT_BY_OPERATOR(false),
	HSE_SITUATIONS_RESPONSE(false),
	PERMIT_FORMAL_REVIEW_RESPONSE(false),
	VARIATION_POWERS_FREE_ALLOCATION_PROVISIONS(false),
	OTHER(true)
	;
	
	private boolean otherSummaryApplies;
}
