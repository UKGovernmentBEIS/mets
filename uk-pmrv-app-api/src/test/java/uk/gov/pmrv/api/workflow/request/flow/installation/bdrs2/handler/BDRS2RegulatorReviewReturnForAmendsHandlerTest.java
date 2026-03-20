package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RegulatorReviewReturnForAmendsTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2RegulatorReviewSubmitService;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BDRS2RegulatorReviewReturnForAmendsHandlerTest {

    @InjectMocks
    private BDRS2RegulatorReviewReturnForAmendsHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private BDRS2RegulatorReviewSubmitService submitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final long taskId = 1L;
        final AppUser user = AppUser.builder().build();
        final BDRS2RegulatorReviewReturnForAmendsTaskActionPayload payload = BDRS2RegulatorReviewReturnForAmendsTaskActionPayload
                .builder()
                .payloadType(RequestTaskActionPayloadType.BDRS2_REGULATOR_REVIEW_RETURN_FOR_AMENDS_PAYLOAD)
                .build();
        final String processId = "processId";
        final String requestId = "requestId";
        final BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload.builder().build();
        final RequestTask task = RequestTask.builder()
                .request(Request.builder().id(requestId).build())
                .type(RequestTaskType.BDRS2_APPLICATION_REGULATOR_REVIEW_SUBMIT)
                .processTaskId(processId)
                .payload(taskPayload)
                .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(task);

        handler.process(taskId, RequestTaskActionType.BDRS2_REGULATOR_REVIEW_RETURN_FOR_AMENDS, user, payload);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(submitService, times(1)).returnForAmends(task, user);
        verify(workflowService, times(1)).completeTask(processId,
                Map.of(BpmnProcessConstants.BDRS2_REGULATOR_REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name()));
    }

    @Test
    void getTypes() {
        Assertions.assertEquals(List.of(RequestTaskActionType.BDRS2_REGULATOR_REVIEW_RETURN_FOR_AMENDS), handler.getTypes());
    }
}
