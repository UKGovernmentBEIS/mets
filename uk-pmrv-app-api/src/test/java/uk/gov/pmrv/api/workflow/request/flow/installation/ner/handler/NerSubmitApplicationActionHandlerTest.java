package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplyService;

@ExtendWith(MockitoExtension.class)
class NerSubmitApplicationActionHandlerTest {

    @InjectMocks
    private NerSubmitApplicationActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NerApplyService applyService;

    @Mock
    private DateService dateService;

    @Mock
    private WorkflowService workflowService;


    @Test
    void doProcess() {

        final RequestTaskActionEmptyPayload taskActionPayload =
            RequestTaskActionEmptyPayload.builder()
                .payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD)
                .build();
        final AppUser appUser = AppUser.builder().build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final RequestTask requestTask =
            RequestTask.builder().id(1L).request(request).processTaskId(processTaskId).build();
        final LocalDateTime submissionDate = LocalDateTime.of(2022, 1, 1, 1, 1);

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        when(dateService.getLocalDateTime()).thenReturn(submissionDate);

        handler.process(requestTask.getId(),
            RequestTaskActionType.NER_SUBMIT_APPLICATION,
            appUser,
            taskActionPayload);

        Assertions.assertEquals(request.getSubmissionDate(), submissionDate);
        verify(applyService, times(1)).applySubmitAction(requestTask, appUser);
        verify(workflowService, times(1)).completeTask(processTaskId,
            Map.of(
                BpmnProcessConstants.NER_SUBMIT_OUTCOME, NerSubmitOutcome.SUBMITTED
            ));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).isEqualTo(List.of(RequestTaskActionType.NER_SUBMIT_APPLICATION));
    }
}
