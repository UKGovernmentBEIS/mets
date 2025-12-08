package uk.gov.pmrv.api.integration.registry.accountcreated.aviation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationAccountCreatedRegistryEventListener;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationAccountCreatedNotifyRegistryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationAccountCreatedRegistryEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AviationAccountCreatedRegistryEventListenerTest {

    @Mock
    private AviationAccountCreatedNotifyRegistryService aviationAccountCreatedNotifyRegistryService;

    @InjectMocks
    private AviationAccountCreatedRegistryEventListener aviationAccountCreatedRegistryEventListener;

    @Test
    void handle_delegates_to_service_with_event() {
        AviationAccountCreatedRegistryEvent event = AviationAccountCreatedRegistryEvent.builder()
                .accountId(1L)
                .requestId("REQ-1")
                .build();

        aviationAccountCreatedRegistryEventListener.handle(event);

        verify(aviationAccountCreatedNotifyRegistryService, times(1)).notifyRegistry(event);
    }
}