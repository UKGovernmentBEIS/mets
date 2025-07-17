package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonCompliancePenalties;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.validation.NonComplianceApplicationValidator;

@ExtendWith(MockitoExtension.class)
class NonComplianceSubmitApplicationActionHandlerTest {

    @InjectMocks
    private NonComplianceSubmitApplicationActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NonComplianceApplicationValidator validator;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestService requestService;

    @Test
    void process() {

        final long requestTaskId = 1L;
        final String processTaskId = "processTaskId";
        final NonComplianceApplicationSubmitRequestTaskPayload taskPayload =
            NonComplianceApplicationSubmitRequestTaskPayload.builder()
                .nonCompliancePenalties(NonCompliancePenalties.builder()
                    .civilPenalty(true)
                    .noticeOfIntent(false)
                    .dailyPenalty(false).build()).build();
        final Request request = Request.builder().id("reqid").payload(NonComplianceRequestPayload.builder().build()).build();
        final RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(request)
            .payload(taskPayload)
            .processTaskId(processTaskId)
            .build();
        final RequestTaskActionEmptyPayload taskActionPayload =
            RequestTaskActionEmptyPayload.builder().build();
        final AppUser appUser = AppUser.builder().userId("userId").build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, RequestTaskActionType.NON_COMPLIANCE_SUBMIT_APPLICATION, appUser,
            taskActionPayload);

        verify(validator, times(1)).validateApplication(taskPayload);
        verify(requestService, times(1)).addActionToRequest(
            request,
            NonComplianceApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.NON_COMPLIANCE_APPLICATION_SUBMITTED_PAYLOAD)
                .nonCompliancePenalties(NonCompliancePenalties.builder()
                    .civilPenalty(true)
                    .noticeOfIntent(false)
                    .dailyPenalty(false).build()).build(),
            RequestActionType.NON_COMPLIANCE_APPLICATION_SUBMITTED,
            "userId"
        );
        verify(workflowService, times(1)).completeTask(
            processTaskId, Map.of(BpmnProcessConstants.NON_COMPLIANCE_OUTCOME, NonComplianceOutcome.SUBMITTED,
                BpmnProcessConstants.CIVIL_PENALTY_LIABLE, true,
                BpmnProcessConstants.DAILY_PENALTY_LIABLE, false,
                BpmnProcessConstants.NOI_PENALTY_LIABLE, false
            )
        );
        assertThat(request.getSubmissionDate()).isNotNull();
        assertThat(((NonComplianceRequestPayload)request.getPayload()).getIssueNoticeOfIntent()).isFalse();
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.NON_COMPLIANCE_SUBMIT_APPLICATION);
    }
}
