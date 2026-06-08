package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRegulatorReviewReturnForAmendsTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NERRegulatorReviewSubmitService;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NERRegulatorReviewReturnForAmendsHandlerTest {

    @InjectMocks
    private NERRegulatorReviewReturnForAmendsHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NERRegulatorReviewSubmitService submitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final long taskId = 1L;
        final AppUser user = AppUser.builder().build();
        final NERRegulatorReviewReturnForAmendsTaskActionPayload payload = NERRegulatorReviewReturnForAmendsTaskActionPayload
                .builder()
                .payloadType(RequestTaskActionPayloadType.NER_REGULATOR_REVIEW_RETURN_FOR_AMENDS_PAYLOAD)
                .build();
        final String processId = "processId";
        final String requestId = "requestId";
        final NERApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                NERApplicationRegulatorReviewSubmitRequestTaskPayload.builder().build();
        final RequestTask task = RequestTask.builder()
                .request(Request.builder().id(requestId).build())
                .type(RequestTaskType.NER_APPLICATION_REVIEW)
                .processTaskId(processId)
                .payload(taskPayload)
                .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(task);

        handler.process(taskId, RequestTaskActionType.NER_REGULATOR_REVIEW_RETURN_FOR_AMENDS, user, payload);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(submitService, times(1)).returnForAmends(task, user);
        verify(workflowService, times(1)).completeTask(processId,
                Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name()));
    }

    @Test
    void getTypes() {
        Assertions.assertEquals(List.of(RequestTaskActionType.NER_REGULATOR_REVIEW_RETURN_FOR_AMENDS), handler.getTypes());
    }
}
