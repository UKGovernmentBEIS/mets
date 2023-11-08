package uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;

@Service
@RequiredArgsConstructor
class PermitReissueUpdatePayloadConsolidationNumberService {

	private final PermitQueryService permitQueryService;
	
	@Transactional
	public void updateRequestPayloadConsolidationNumber(Request request) {
		final ReissueRequestPayload requestPayload = (ReissueRequestPayload) request.getPayload();
		int consolidationNumber = permitQueryService.getPermitConsolidationNumberByAccountId(request.getAccountId());
		requestPayload.setConsolidationNumber(consolidationNumber);
	}
	
}
