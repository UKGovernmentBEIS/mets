package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class ReturnOfAllowancesApplicationCancelledService {

    private final RequestService requestService;

    public void cancel(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final String assignee = request.getPayload().getRegulatorAssignee();

        requestService.addActionToRequest(request,
            null,
            RequestActionType.RETURN_OF_ALLOWANCES_APPLICATION_CANCELLED,
            assignee);
    }
}
