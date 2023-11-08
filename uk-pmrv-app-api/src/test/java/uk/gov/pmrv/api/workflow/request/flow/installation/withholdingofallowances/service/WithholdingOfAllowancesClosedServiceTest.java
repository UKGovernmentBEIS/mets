package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationClosedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesClosedServiceTest {

    @Mock
    private RequestService requestService;

    @InjectMocks
    private WithholdingOfAllowancesClosedService withholdingOfAllowancesClosedService;

    @Test
    void close() {
        String requestId = "123";
        Request request = new Request();
        WithholdingOfAllowancesRequestPayload requestPayload = new WithholdingOfAllowancesRequestPayload();
        request.setPayload(requestPayload);
        when(requestService.findRequestById(requestId)).thenReturn(request);

        withholdingOfAllowancesClosedService.close(requestId);

        verify(requestService).addActionToRequest(eq(request),
            any(WithholdingOfAllowancesApplicationClosedRequestActionPayload.class),
            eq(RequestActionType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_CLOSED),
            any());
    }
}
