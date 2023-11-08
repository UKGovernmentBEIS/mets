package uk.gov.pmrv.api.workflow.request.flow.common.vir.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;

@ExtendWith(MockitoExtension.class)
class VirDeadlineServiceTest {

    @InjectMocks
    private VirDeadlineService service;

    @Mock
    private RequestService requestService;

    @Mock
    private VirRespondToRegulatorCommentsNotificationService virRespondToRegulatorCommentsNotificationService;

    @Test
    void sendDeadlineNotification() {
        final String requestId = "requestId";
        final String reviewer = "regulator";
        final long accountId = 1L;
        final Request request = Request.builder()
                .accountId(accountId)
                .payload(VirRequestPayload.builder()
                        .regulatorReviewer(reviewer)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.sendDeadlineNotification(requestId);

        // Verify
        verify(virRespondToRegulatorCommentsNotificationService, times(1))
                .sendDeadlineResponseToRegulatorCommentsNotificationToRegulator(request);
    }
}
