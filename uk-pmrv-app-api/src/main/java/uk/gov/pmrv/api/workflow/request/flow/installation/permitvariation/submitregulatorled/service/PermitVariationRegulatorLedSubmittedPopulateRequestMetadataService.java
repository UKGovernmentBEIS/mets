package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;

@Service
@RequiredArgsConstructor
public class PermitVariationRegulatorLedSubmittedPopulateRequestMetadataService {

	private final RequestService requestService;
	private final PermitQueryService permitQueryService;
	
	@Transactional
	public void populateRequestMetadata(String requestId) {
		final Request request = requestService.findRequestById(requestId);
		
		final PermitVariationRequestMetadata requestMetadata = (PermitVariationRequestMetadata) request.getMetadata();
		
		final PermitVariationRequestPayload requestPayload = (PermitVariationRequestPayload) request.getPayload();
		PermitVariationRegulatorLedGrantDetermination determination = requestPayload.getDeterminationRegulatorLed();
		requestMetadata.setLogChanges(determination.getLogChanges());
		
		int permitConsolidationNumber = permitQueryService.getPermitConsolidationNumberByAccountId(request.getAccountId());
		requestMetadata.setPermitConsolidationNumber(permitConsolidationNumber);
		requestPayload.setPermitConsolidationNumber(permitConsolidationNumber);
	}
}
