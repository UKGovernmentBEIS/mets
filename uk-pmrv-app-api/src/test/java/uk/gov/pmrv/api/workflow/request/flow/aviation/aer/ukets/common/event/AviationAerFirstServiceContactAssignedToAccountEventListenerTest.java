package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.account.domain.event.FirstServiceContactAssignedToAccountEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.event.AviationAerFirstServiceContactAssignedToAccountEventListener;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerContactUpdateService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AviationAerFirstServiceContactAssignedToAccountEventListenerTest {

    @InjectMocks
    private AviationAerFirstServiceContactAssignedToAccountEventListener listener;

    @Mock
    private AviationAerContactUpdateService serviceContactUpdateService;

    @Test
    void onFirstServiceContactAssignedToAccountEvent() {

        Long accountId = 1L;

        FirstServiceContactAssignedToAccountEvent event = FirstServiceContactAssignedToAccountEvent.builder()
                .accountId(accountId)
                .serviceContactDetails(ServiceContactDetails.builder().build())
                .build();
        listener.onFirstServiceContactAssignedToAccountEvent(event);

        verify(serviceContactUpdateService, times(1))
                .updateAerAviationApplicationSubmitTaskPayloadWithServiceContactDetails(accountId, ServiceContactDetails.builder().build());
    }
}
