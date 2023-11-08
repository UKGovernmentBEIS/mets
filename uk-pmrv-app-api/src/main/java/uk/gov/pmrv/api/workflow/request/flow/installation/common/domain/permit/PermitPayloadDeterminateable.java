package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit;

import uk.gov.pmrv.api.permit.domain.Permit;

public interface PermitPayloadDeterminateable<S extends Determinateable> {

	Permit getPermit();
	
	S getDetermination();
	
	void setDetermination(S determination);
	
}
