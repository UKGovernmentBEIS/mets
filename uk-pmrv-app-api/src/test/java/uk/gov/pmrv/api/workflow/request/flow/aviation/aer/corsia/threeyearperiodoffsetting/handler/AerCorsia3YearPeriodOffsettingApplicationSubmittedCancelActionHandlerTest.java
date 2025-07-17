package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.enumeration.AviationAerCorsia3YearPeriodOffsettingSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
public class AerCorsia3YearPeriodOffsettingApplicationSubmittedCancelActionHandlerTest {

    @InjectMocks
    private AerCorsia3YearPeriodOffsettingApplicationSubmittedCancelActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private AppUser appUser;

    @Test
    void testProcess() {
        // Given
        Long requestTaskId = 1L;
        String processTaskId = "processTaskId";
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_CANCEL_APPLICATION;
        RequestTaskActionEmptyPayload payload = mock(RequestTaskActionEmptyPayload.class);

        RequestTask requestTask = RequestTask
                .builder()
                .id(requestTaskId)
                .processTaskId(processTaskId)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // When
        handler.process(requestTaskId, requestTaskActionType, appUser, payload);

        // Then
        verify(requestTaskService).findTaskById(requestTaskId);
        verify(workflowService).completeTask(
                processTaskId,
                Map.of(BpmnProcessConstants.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_OUTCOME,
                        AviationAerCorsia3YearPeriodOffsettingSubmitOutcome.CANCELLED)
        );
    }

    @Test
    void testGetTypes() {
        // When
        List<RequestTaskActionType> types = handler.getTypes();

        // Then
        assertEquals(1, types.size());
        assertEquals(RequestTaskActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_CANCEL_APPLICATION, types.getFirst());
    }
}
