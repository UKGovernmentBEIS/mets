package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrender;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.RequestPermitSurrenderService;

import java.time.LocalDate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderApplySaveActionHandlerTest {

    @InjectMocks
    private PermitSurrenderApplySaveActionHandler handler;
    
    @Mock
    private RequestPermitSurrenderService requestPermitSurrenderService;
    
    @Mock
    private RequestTaskService requestTaskService;
    
    @Test
    void process() {
        PermitSurrenderSaveApplicationRequestTaskActionPayload actionPayload = 
                PermitSurrenderSaveApplicationRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.PERMIT_SURRENDER_SAVE_APPLICATION_PAYLOAD)
                        .permitSurrender(PermitSurrender.builder().stopDate(LocalDate.now().minusDays(1)).build())
                        .build();
        
        RequestTask requestTask = RequestTask.builder().id(1L).build();
        AppUser appUser = AppUser.builder().build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        
        //invoke
        handler.process(requestTask.getId(), RequestTaskActionType.PERMIT_SURRENDER_SAVE_APPLICATION, appUser, actionPayload);
        
        //verify
        verify(requestPermitSurrenderService, times(1)).applySavePayload(actionPayload, requestTask);
    }
}
