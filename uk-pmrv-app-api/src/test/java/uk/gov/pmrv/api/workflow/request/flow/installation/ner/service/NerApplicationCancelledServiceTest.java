package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplicationCancelledService;

@ExtendWith(MockitoExtension.class)
class NerApplicationCancelledServiceTest {

    @InjectMocks
    private NerApplicationCancelledService service;

    @Mock
    private RequestService requestService;

    @Test
    void cancel() {

        final String requestId = "1";
        final NerRequestPayload requestPayload = NerRequestPayload.builder()
            .payloadType(RequestPayloadType.NER_REQUEST_PAYLOAD)
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
            RequestActionType.NER_APPLICATION_CANCELLED,
            "operatorAssignee");
    }
}
