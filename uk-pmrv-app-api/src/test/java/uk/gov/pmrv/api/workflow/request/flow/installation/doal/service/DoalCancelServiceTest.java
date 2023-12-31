package uk.gov.pmrv.api.workflow.request.flow.installation.doal.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalCancelServiceTest {

    @InjectMocks
    private DoalCancelService service;

    @Mock
    private RequestService requestService;

    @Test
    void cancel() {
        final String requestId = "AEM";
        final String regulatorAssignee = "reg";

        final Request request = Request.builder()
                .id(requestId)
                .payload(DoalRequestPayload.builder()
                        .regulatorAssignee(regulatorAssignee)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.cancel(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(
                request, null, RequestActionType.DOAL_APPLICATION_CANCELLED, regulatorAssignee);
    }
}
