package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class PermitNotificationCancelledService {

    private final RequestService requestService;

    public void cancel(final String requestId) {
        final Request request = requestService.findRequestById(requestId);

        requestService.addActionToRequest(request, null,
                RequestActionType.PERMIT_NOTIFICATION_APPLICATION_CANCELLED,
                request.getPayload().getOperatorAssignee());
    }
}
