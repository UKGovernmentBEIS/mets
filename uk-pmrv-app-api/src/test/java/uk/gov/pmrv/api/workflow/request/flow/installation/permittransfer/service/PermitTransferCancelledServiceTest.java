package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferCancelledService;

@ExtendWith(MockitoExtension.class)
class PermitTransferCancelledServiceTest {

    @InjectMocks
    private PermitTransferCancelledService service;

    @Mock
    private RequestService requestService;

    @Test
    void cancel() {

        final String requestId = "1";
        final PermitTransferARequestPayload requestPayload = PermitTransferARequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_TRANSFER_A_REQUEST_PAYLOAD)
            .operatorAssignee("operatorAssignee")
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .payload(requestPayload)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.cancel(requestId);

        verify(requestService, times(1)).addActionToRequest(
            request,
            null,
            RequestActionType.PERMIT_TRANSFER_APPLICATION_CANCELLED,
            "operatorAssignee");
    }
}
