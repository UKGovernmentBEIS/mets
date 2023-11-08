package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesRequestPayload;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnOfAllowancesApplicationCancelledServiceTest {

    @Mock
    private RequestService requestService;

    @InjectMocks
    private ReturnOfAllowancesApplicationCancelledService service;

    @Test
    void cancel_shouldAddActionToRequestWithCancelledType() {
        String requestId = "123";
        Request request = Request.builder()
            .payload(ReturnOfAllowancesRequestPayload.builder()
                .regulatorAssignee("assignee")
                .build())
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.cancel(requestId);

        verify(requestService).addActionToRequest(
            request,
            null,
            RequestActionType.RETURN_OF_ALLOWANCES_APPLICATION_CANCELLED,
            "assignee"
        );
    }
}
