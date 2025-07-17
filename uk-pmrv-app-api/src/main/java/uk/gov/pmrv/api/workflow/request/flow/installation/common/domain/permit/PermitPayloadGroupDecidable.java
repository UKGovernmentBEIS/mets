package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit;

import uk.gov.pmrv.api.permit.domain.Permit;

import java.util.Map;

public interface PermitPayloadGroupDecidable<T extends PermitReviewDecision> {

	Permit getPermit();
	
	Map<PermitReviewGroup, T> getReviewGroupDecisions();
	
}
