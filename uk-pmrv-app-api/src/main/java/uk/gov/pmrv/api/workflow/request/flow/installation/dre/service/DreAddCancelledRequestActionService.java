package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class DreAddCancelledRequestActionService {

	private final RequestService requestService;
	
	@Transactional
	public void add(final String requestId) {
		final Request request = requestService.findRequestById(requestId);

        requestService.addActionToRequest(request,
            null,
            RequestActionType.DRE_APPLICATION_CANCELLED,
            request.getPayload().getRegulatorAssignee());
	}
}
