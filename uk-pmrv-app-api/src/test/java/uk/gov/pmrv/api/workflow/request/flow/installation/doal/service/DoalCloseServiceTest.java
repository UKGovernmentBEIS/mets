package uk.gov.pmrv.api.workflow.request.flow.installation.doal.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.Doal;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationClosedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalClosedDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalDeterminationType;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalCloseServiceTest {

    @InjectMocks
    private DoalCloseService service;

    @Mock
    private RequestService requestService;

    @Test
    void addClosedRequestAction() {
        final String requestId = "AEM";
        final Long accountId = 1L;
        final String regulatorAssignee = "regulatorAssignee";

        final DoalClosedDetermination determination = DoalClosedDetermination.builder()
                .type(DoalDeterminationType.CLOSED)
                .reason("Close reason")
                .build();
        final Doal doal = Doal.builder()
            .determination(determination)
            .build();
        final Request request = Request.builder()
                .accountId(accountId)
                .payload(DoalRequestPayload.builder()
                        .regulatorAssignee(regulatorAssignee)
                        .doal(doal)
                        .build())
                .build();

        final DoalApplicationClosedRequestActionPayload actionPayload =
                DoalApplicationClosedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.DOAL_APPLICATION_CLOSED_PAYLOAD)
                        .doal(doal)
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.addClosedRequestAction(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1))
                .addActionToRequest(request, actionPayload, RequestActionType.DOAL_APPLICATION_CLOSED, regulatorAssignee);
    }
}
