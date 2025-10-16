package uk.gov.pmrv.api.integration.registry.accountcreated.aviation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.EmpIssuanceApprovedEventListener;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.EmpIssuanceApprovedNotifyRegistryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceApprovedEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceApprovedEventListenerTest {

    @Mock
    private EmpIssuanceApprovedNotifyRegistryService empIssuanceApprovedNotifyRegistryService;

    @InjectMocks
    private EmpIssuanceApprovedEventListener empIssuanceApprovedEventListener;

    @Test
    void handle_delegates_to_service_with_event() {
        EmpIssuanceApprovedEvent event = EmpIssuanceApprovedEvent.builder()
                .accountId(1L)
                .requestId("REQ-1")
                .build();

        empIssuanceApprovedEventListener.handle(event);

        verify(empIssuanceApprovedNotifyRegistryService, times(1)).notifyRegistry(event);
    }
}