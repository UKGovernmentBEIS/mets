package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationGrantDetermination;

@Service
@RequiredArgsConstructor
public class PermitVariationGrantedPopulateRequestMetadataService {

	private final RequestService requestService;
	private final PermitQueryService permitQueryService;
	
	@Transactional
	public void populateRequestMetadata(String requestId) {
		final Request request = requestService.findRequestById(requestId);
		
		final PermitVariationRequestMetadata requestMetadata = (PermitVariationRequestMetadata) request.getMetadata();
		
		final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();
		PermitVariationGrantDetermination grantDetermination = (PermitVariationGrantDetermination) requestPayload.getDetermination();
		requestMetadata.setLogChanges(grantDetermination.getLogChanges());
		
		int permitConsolidationNumber = permitQueryService.getPermitConsolidationNumberByAccountId(request.getAccountId());
		requestMetadata.setPermitConsolidationNumber(permitConsolidationNumber);
		requestPayload.setPermitConsolidationNumber(permitConsolidationNumber);
	}
}
