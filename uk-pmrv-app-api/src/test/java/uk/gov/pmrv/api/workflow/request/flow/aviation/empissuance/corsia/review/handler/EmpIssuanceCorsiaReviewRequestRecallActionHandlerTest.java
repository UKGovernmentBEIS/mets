package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceCorsiaReviewRequestRecallActionHandlerTest {

    @InjectMocks
    private EmpIssuanceCorsiaReviewRequestRecallActionHandler recallActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void getTypes() {
        assertThat(recallActionHandler.getTypes()).containsExactly(RequestTaskActionType.EMP_ISSUANCE_CORSIA_RECALL_FROM_AMENDS);
    }

    @Test
    void getRequestActionType() {
        assertEquals(RequestActionType.EMP_ISSUANCE_CORSIA_RECALLED_FROM_AMENDS, recallActionHandler.getRequestActionType());
    }

    @Test
    void process() {
        AppUser appUser = AppUser.builder().userId("userId").build();
        String processTaskId = "processTaskId";
        Request request = Request.builder().id("requestId")
            .payload(EmpIssuanceCorsiaRequestPayload.builder().build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .processTaskId(processTaskId)
            .request(request)
            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        recallActionHandler.process(requestTask.getId(),
            RequestTaskActionType.EMP_ISSUANCE_CORSIA_RECALL_FROM_AMENDS,
            appUser,
            RequestTaskActionEmptyPayload.builder().build());

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(requestService, times(1)).addActionToRequest(request,
            null,
            RequestActionType.EMP_ISSUANCE_CORSIA_RECALLED_FROM_AMENDS,
            "userId");
        verify(workflowService, times(1)).completeTask("processTaskId");
    }
}