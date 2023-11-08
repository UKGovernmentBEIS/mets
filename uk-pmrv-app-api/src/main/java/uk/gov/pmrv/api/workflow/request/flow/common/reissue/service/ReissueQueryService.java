package uk.gov.pmrv.api.workflow.request.flow.common.reissue.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;

@Service
@RequiredArgsConstructor
public class ReissueQueryService {

	private final RequestService requestService;
	
	public Request getBatchRequest(Request reissueRequest) {
		final ReissueRequestMetadata metadata = (ReissueRequestMetadata) reissueRequest.getMetadata();
		final String batchReissueId = metadata.getBatchRequestId();
		return requestService.findRequestById(batchReissueId);
	}
}
