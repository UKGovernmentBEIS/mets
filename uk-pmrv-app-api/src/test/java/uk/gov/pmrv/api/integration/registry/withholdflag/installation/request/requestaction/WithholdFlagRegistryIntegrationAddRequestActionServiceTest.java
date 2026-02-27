package uk.gov.pmrv.api.integration.registry.withholdflag.installation.request.requestaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEvent;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import java.time.Year;

import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WithholdFlagRegistryIntegrationAddRequestActionServiceTest {

    @Mock
    private RequestService requestService;

    @InjectMocks
    private WithholdFlagRegistryIntegrationAddRequestActionService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addRequestAction_shouldAddSystemActionToRequest() {
        String requestId = "req-1";
        Request mockRequest = mock(Request.class);
        AccountWithholdUpdateEvent event = mock(AccountWithholdUpdateEvent.class);
        when(requestService.findRequestById(requestId)).thenReturn(mockRequest);
        when(event.getWithholdFlag()).thenReturn(true);
        when(event.getRegistryId()).thenReturn(123L);
        when(event.getReportingYear()).thenReturn(Year.of(2025));

        service.addRequestAction(requestId, event);

        verify(requestService, times(1)).addSystemActionToRequest(
                eq(mockRequest),
                argThat(payload -> payload instanceof WithholdingOfAllowancesRegistryIntegrationRequestActionPayload &&
                        ((WithholdingOfAllowancesRegistryIntegrationRequestActionPayload) payload).getWithholdFlag().equals(true) &&
                        ((WithholdingOfAllowancesRegistryIntegrationRequestActionPayload) payload).getRegistryId().equals(123) &&
                        ((WithholdingOfAllowancesRegistryIntegrationRequestActionPayload) payload).getWithholdYear().equals(Year.of(2025)) &&
                        ((WithholdingOfAllowancesRegistryIntegrationRequestActionPayload) payload).getPayloadType() == RequestActionPayloadType.WITHHOLDING_OF_ALLOWANCES_REGISTRY_INTEGRATION_PAYLOAD
                ),
                eq(RequestActionType.WITHHOLDING_OF_ALLOWANCES_SENT_TO_REGISTRY)
        );
    }
}