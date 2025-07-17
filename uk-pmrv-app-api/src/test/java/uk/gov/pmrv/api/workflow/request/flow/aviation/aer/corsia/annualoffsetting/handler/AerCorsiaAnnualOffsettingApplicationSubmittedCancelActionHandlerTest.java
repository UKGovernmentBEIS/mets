package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.enumeration.AviationAerCorsiaAnnualOffsettingSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AerCorsiaAnnualOffsettingApplicationSubmittedCancelActionHandlerTest {

    @InjectMocks
    private AerCorsiaAnnualOffsettingApplicationSubmittedCancelActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestTask requestTask;

    @Mock
    private AppUser appUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcess() {
        // Given
        Long requestTaskId = 1L;
        String processTaskId = "processTaskId";
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_CANCEL_APPLICATION;
        RequestTaskActionEmptyPayload payload = mock(RequestTaskActionEmptyPayload.class);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(requestTask.getProcessTaskId()).thenReturn(processTaskId);

        // When
        handler.process(requestTaskId, requestTaskActionType, appUser, payload);

        // Then
        verify(requestTaskService).findTaskById(requestTaskId);
        verify(workflowService).completeTask(
                processTaskId,
                Map.of(BpmnProcessConstants.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_OUTCOME,
                        AviationAerCorsiaAnnualOffsettingSubmitOutcome.CANCELLED)
        );
    }

    @Test
    void testGetTypes() {
        // When
        List<RequestTaskActionType> types = handler.getTypes();

        // Then
        assertEquals(1, types.size());
        assertEquals(RequestTaskActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_CANCEL_APPLICATION, types.get(0));
    }
}
