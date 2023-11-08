package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.event;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.account.domain.event.FirstServiceContactAssignedToAccountEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.service.EmpContactUpdateService;

@ExtendWith(MockitoExtension.class)
class EmpFirstServiceContactAssignedToAccountEventListenerTest {

    @InjectMocks
    private EmpFirstServiceContactAssignedToAccountEventListener listener;

    @Mock
    private EmpContactUpdateService serviceContactUpdateService;

    @Test
    void onFirstServiceContactAssignedToAccountEvent() {

        Long accountId = 1L;

        FirstServiceContactAssignedToAccountEvent event = FirstServiceContactAssignedToAccountEvent.builder()
                .accountId(accountId)
                .serviceContactDetails(ServiceContactDetails.builder().build())
                .build();
        listener.onFirstServiceContactAssignedToAccountEvent(event);

        verify(serviceContactUpdateService, times(1))
                .updateEmpApplicationSubmitTaskPayloadWithServiceContactDetails(accountId, ServiceContactDetails.builder().build());
    }
}
