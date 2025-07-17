package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerReviewValidatorService;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerCompleteActionHandlerTest {

    @InjectMocks
    private AerCompleteActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AerReviewService aerReviewService;

    @Mock
    private AerReviewValidatorService aerReviewValidatorService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final long taskId = 1L;
        final String requestId = "requestId";
        final AppUser user = AppUser.builder().build();
        final String process = "process";

        final RequestTaskActionEmptyPayload payload = RequestTaskActionEmptyPayload.builder().build();

        final Aer aer = Aer.builder().build();
        final Map<AerReviewGroup, AerReviewDecision> reviewGroupDecisions = Map.of(
            AerReviewGroup.ADDITIONAL_INFORMATION,
            AerDataReviewDecision.builder()
                .reviewDataType(AerReviewDataType.AER_DATA)
                .type(AerDataReviewDecisionType.ACCEPTED)
                .build()

        );
        final AerApplicationReviewRequestTaskPayload taskPayload = AerApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AER_APPLICATION_REVIEW_PAYLOAD)
            .permitOriginatedData(PermitOriginatedData.builder().permitType(PermitType.GHGE).build())
            .verificationReport(AerVerificationReport.builder()
                .verificationData(AerVerificationData.builder()
                    .build())
                .build())
            .aer(aer)
            .reviewGroupDecisions(reviewGroupDecisions)
            .build();
        final AerRequestPayload aerRequestPayload = AerRequestPayload.builder()
            .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
            .verificationPerformed(true)
            .build();

        final RequestTask task = RequestTask.builder()
            .id(taskId)
            .processTaskId(process)
            .payload(taskPayload)
            .request(Request.builder()
                .id(requestId)
                .type(RequestType.AER)
                .payload(aerRequestPayload)
                .build())
            .build();

        final Map<String, Object> variables = Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
            BpmnProcessConstants.AER_REVIEW_OUTCOME, ReviewOutcome.COMPLETED);

        when(requestTaskService.findTaskById(taskId)).thenReturn(task);

        // Invoke
        handler.process(taskId, RequestTaskActionType.AER_COMPLETE_REVIEW, user, payload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(aerReviewValidatorService, times(1)).validateCompleted(aer, reviewGroupDecisions, true,
            taskPayload.getVerificationReport());
        verify(aerReviewService, times(1)).updateRequestPayload(task, user);
        verify(workflowService, times(1)).completeTask(process, variables);
    }

    @Test
    void getTypes() {
        Assertions.assertEquals(List.of(RequestTaskActionType.AER_COMPLETE_REVIEW), handler.getTypes());
    }
}
