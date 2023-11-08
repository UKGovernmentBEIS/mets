package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class AirDeadlineService {

    private final RequestService requestService;
    private final AirRespondToRegulatorCommentsNotificationService respondToRegulatorCommentsNotificationService;

    public void sendDeadlineNotification(final String requestId) {
        
        final Request request = requestService.findRequestById(requestId);
        respondToRegulatorCommentsNotificationService.sendDeadlineResponseToRegulatorCommentsNotificationToRegulator(request);
    }
}
