package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationClosedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRClosedDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.enums.DoalDeterminationType;

import java.util.HashSet;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ALRCloseServiceTest {

    @InjectMocks
    private ALRCloseService service;

    @Mock
    private RequestService requestService;

    @Test
    void addClosedRequestAction() {
        final String requestId = "AEM";
        final Long accountId = 1L;
        final String regulatorAssignee = "regulatorAssignee";

        final ALRClosedDetermination determination = ALRClosedDetermination.builder()
                .type(DoalDeterminationType.CLOSED_ALR)
                .reason("Close reason")
                .files(new HashSet<>())
                .build();

        final Request request = Request.builder()
                .accountId(accountId)
                .payload(ALRRequestPayload.builder()
                        .regulatorAssignee(regulatorAssignee)
                        .regulatorReviewOutcome(ALRApplicationRegulatorReviewOutcome.builder().build())
                        .build())
                .build();

            final ALRApplicationClosedRequestActionPayload actionPayload =
                    ALRApplicationClosedRequestActionPayload.builder()
                        .regulatorReviewOutcome(ALRApplicationRegulatorReviewOutcome.builder().build())
                        .payloadType(RequestActionPayloadType.ALR_APPLICATION_CLOSED_PAYLOAD)
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.addClosedRequestAction(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1))
                .addActionToRequest(request, actionPayload, RequestActionType.ALR_APPLICATION_CLOSED, regulatorAssignee);
    }
}
