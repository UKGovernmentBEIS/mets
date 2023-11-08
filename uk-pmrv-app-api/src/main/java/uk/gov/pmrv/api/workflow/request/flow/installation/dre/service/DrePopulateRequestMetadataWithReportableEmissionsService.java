package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;

@Service
@RequiredArgsConstructor
public class DrePopulateRequestMetadataWithReportableEmissionsService {
	
	private final RequestService requestService;

	@Transactional
	public void updateRequestMetadata(String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final DreRequestPayload requestPayload = (DreRequestPayload)request.getPayload();
		final DreRequestMetadata metadata = (DreRequestMetadata)request.getMetadata();
		metadata.setEmissions(requestPayload.getDre().getTotalReportableEmissions());
	}
}
