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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplyService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NERApplicationSubmitToRegulatorActionHandlerTest {

    @InjectMocks
    private NERApplicationSubmitToRegulatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NerApplyService nerApplyService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process_shouldSubmitAndCompleteWorkflow() {
        // given
        Long requestTaskId = 1L;

        Request request = Request.builder()
                .id("id")
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId("process-id")
                .build();

        AppUser appUser = AppUser.builder().userId("user").build();

        when(requestTaskService.findTaskById(requestTaskId))
                .thenReturn(requestTask);

        // when
        handler.process(
                requestTaskId,
                RequestTaskActionType.NER_SUBMIT_APPLICATION,
                appUser,
                new RequestTaskActionEmptyPayload()
        );

        // then
        verify(nerApplyService).submitToRegulator(requestTask, appUser, RequestActionType.NER_APPLICATION_SUBMITTED);

        assertNotNull(request.getSubmissionDate());

        verify(workflowService).completeTask(
                eq("process-id"),
                eq(Map.of(
                        BpmnProcessConstants.REQUEST_ID, "id",
                        BpmnProcessConstants.NER_SUBMIT_OUTCOME, NerSubmitOutcome.SUBMITTED
                ))
        );
    }

    @Test
    void getTypes_shouldReturnCorrectType() {
        // when
        List<RequestTaskActionType> result = handler.getTypes();

        // then
        assertEquals(List.of(RequestTaskActionType.NER_SUBMIT_APPLICATION), result);
    }
}
