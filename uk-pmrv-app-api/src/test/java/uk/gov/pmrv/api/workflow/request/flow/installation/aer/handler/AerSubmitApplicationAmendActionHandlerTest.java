package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.RequestAerSubmitService;

import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerSubmitApplicationAmendActionHandlerTest {

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestAerSubmitService requestAerSubmitService;

    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private AerSubmitApplicationAmendActionHandler actionHandler;

    @Test
    void process() {
        Long requestTaskId = 1L;
        long accountId = 1L;
        String requestId = "1";
        RequestTaskActionType actionType = RequestTaskActionType.AER_SUBMIT_APPLICATION_AMEND;
        AppUser appUser = new AppUser();
        AerSubmitApplicationAmendRequestTaskActionPayload payload =
            new AerSubmitApplicationAmendRequestTaskActionPayload();

        AerApplicationSubmitRequestTaskPayload taskPayload = AerApplicationSubmitRequestTaskPayload.builder()
            .aer(Aer.builder()
                .abbreviations(Abbreviations.builder().build())
                .build())
            .reportingYear(Year.now())
            .build();

        RequestTask requestTask = RequestTask.builder()
            .processTaskId(requestTaskId.toString())
            .request(Request.builder().id(requestId).accountId(accountId).build())
            .payload(taskPayload)
            .build();
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        actionHandler.process(requestTaskId, actionType, appUser, payload);

        verify(requestTaskService).findTaskById(requestTaskId);
        verify(requestAerSubmitService).sendAmendsToRegulator(requestTask, payload, appUser);
        verify(workflowService).completeTask(requestTaskId.toString(), Map.of(BpmnProcessConstants.REQUEST_ID,
            requestId,
            BpmnProcessConstants.AER_OUTCOME, AerOutcome.REVIEW_REQUESTED));
    }

    @Test
    void getTypes() {
        List<RequestTaskActionType> expectedTypes = List.of(RequestTaskActionType.AER_SUBMIT_APPLICATION_AMEND);
        assertEquals(expectedTypes, actionHandler.getTypes());
    }
}
