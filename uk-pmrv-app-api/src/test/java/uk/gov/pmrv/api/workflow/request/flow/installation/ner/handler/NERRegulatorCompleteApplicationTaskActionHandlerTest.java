package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NERRegulatorReviewSubmitService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NERRegulatorCompleteApplicationTaskActionHandlerTest {

    @Mock
    private NERRegulatorReviewSubmitService reviewSubmitService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private NERRegulatorCompleteApplicationTaskActionHandler handler;

    @Test
    void process() {
        Long requestTaskId = 1L;

        Request request = new Request();
        request.setId("REQ1");

        RequestTask requestTask = new RequestTask();
        requestTask.setProcessTaskId("processTaskId");
        requestTask.setRequest(request);

        AppUser appUser = AppUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId))
                .thenReturn(requestTask);

        handler.process(
                requestTaskId,
                RequestTaskActionType.NER_COMPLETE_REVIEW,
                appUser,
                new RequestTaskActionEmptyPayload()
        );

        verify(reviewSubmitService).completeApplication(requestTask, appUser);

        verify(workflowService).completeTask(
                eq("processTaskId"),
                eq(Map.of(
                        BpmnProcessConstants.REQUEST_ID, "REQ1",
                        BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.COMPLETED
                ))
        );
    }

    @Test
    void getTypes() {
        assertEquals(
                List.of(RequestTaskActionType.NER_COMPLETE_REVIEW),
                handler.getTypes()
        );
    }
}
