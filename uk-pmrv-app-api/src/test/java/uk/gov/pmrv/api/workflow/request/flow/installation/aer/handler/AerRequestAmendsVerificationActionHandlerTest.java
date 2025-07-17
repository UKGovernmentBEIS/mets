package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCustomContext;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationRequestVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.RequestAerSubmitService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerRequestAmendsVerificationActionHandlerTest {

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestAerSubmitService requestAerSubmitService;

    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private AerRequestAmendsVerificationActionHandler actionHandler;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String requestId = "1";
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.AER_REQUEST_AMENDS_VERIFICATION;
        AppUser appUser = new AppUser();
        AerApplicationRequestVerificationRequestTaskActionPayload payload =
                new AerApplicationRequestVerificationRequestTaskActionPayload();
        RequestTask requestTask = RequestTask.builder()
                .processTaskId(requestTaskId.toString())
                .request(Request.builder().id(requestId).build())
                .build();
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        actionHandler.process(requestTaskId, requestTaskActionType, appUser, payload);

        verify(requestAerSubmitService).sendAmendsToVerifier(eq(payload), eq(requestTask), eq(appUser));
        verify(workflowService).completeTask(
                eq(requestTask.getProcessTaskId()),
                eq(Map.of(
                        BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.AER_OUTCOME, AerOutcome.VERIFICATION_REQUESTED,
                        BpmnProcessConstants.REQUEST_TYPE_DYNAMIC_TASK_PREFIX, RequestCustomContext.AER_AMEND.getCode()
                ))
        );
    }

    @Test
    void getTypes() {
        List<RequestTaskActionType> types = actionHandler.getTypes();
        assertEquals(List.of(RequestTaskActionType.AER_REQUEST_AMENDS_VERIFICATION), types);
    }
}
