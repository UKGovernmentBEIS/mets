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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderReviewDeterminationGrant;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.RequestPermitSurrenderReviewService;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderReviewSaveDeterminationActionHandlerTest {

    @InjectMocks
    private PermitSurrenderReviewSaveDeterminationActionHandler handler;

    @Mock
    private RequestPermitSurrenderReviewService requestPermitSurrenderReviewService;
    
    @Mock
    private RequestTaskService requestTaskService;
    
    @Test
    void process() {
        final Long requestTaskId = 1L;
        final RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION;
        final AppUser appUser = AppUser.builder().userId("user").build();
        final PermitSurrenderSaveReviewDeterminationRequestTaskActionPayload payload = PermitSurrenderSaveReviewDeterminationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD)
                .reviewDetermination(PermitSurrenderReviewDeterminationGrant.builder().stopDate(LocalDate.now().minusDays(1)).build())
                .build();
        
        String processTaskId = "processTaskId";
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        
        //invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, payload);

        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(requestPermitSurrenderReviewService, times(1)).saveReviewDetermination(payload, requestTask);
    }
    
    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION);
    }
}
