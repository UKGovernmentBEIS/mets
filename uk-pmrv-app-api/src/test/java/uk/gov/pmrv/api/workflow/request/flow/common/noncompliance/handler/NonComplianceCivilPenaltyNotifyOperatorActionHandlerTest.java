package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltyRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceNotifyOperatorRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service.NonComplianceSendOfficialNoticeServiceDelegator;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.validation.NonComplianceApplicationValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

@ExtendWith(MockitoExtension.class)
class NonComplianceCivilPenaltyNotifyOperatorActionHandlerTest {

    @InjectMocks
    private NonComplianceCivilPenaltyNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private NonComplianceApplicationValidator validator;
    
    @Mock
    private RequestService requestService;
    
    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private NonComplianceSendOfficialNoticeServiceDelegator officialNoticeServiceDelegator;
    
    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Test
    void process() {

        final long requestTaskId = 1L;
        final Request request = Request.builder().build();
        final UUID civilPenalty = UUID.randomUUID();
        final NonComplianceCivilPenaltyRequestTaskPayload taskPayload =
            NonComplianceCivilPenaltyRequestTaskPayload.builder().civilPenalty(civilPenalty).build();
        final RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(taskPayload)
            .processTaskId("processTaskId")
            .request(request)
            .build();
        final Set<String> operators = Set.of("operator");
        final NonComplianceDecisionNotification decisionNotification =
            NonComplianceDecisionNotification.builder().operators(operators).build();
        final NonComplianceNotifyOperatorRequestTaskActionPayload taskActionPayload =
            NonComplianceNotifyOperatorRequestTaskActionPayload.builder()
                .decisionNotification(decisionNotification)
                .build();
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final Map<String, RequestActionUserInfo> userInfo =
            Map.of("operator", RequestActionUserInfo.builder().name("operator name").build());
        final NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload actionPayload =
            NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED_PAYLOAD)
                .civilPenalty(civilPenalty)
                .decisionNotification(decisionNotification)
                .usersInfo(userInfo)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.of(
        		UserInfoDTO.builder().firstName("operator").build()));
        when(requestActionUserInfoResolver.getUsersInfo(operators, request)).thenReturn(userInfo);
        
        handler.process(requestTaskId,
            RequestTaskActionType.NON_COMPLIANCE_CIVIL_PENALTY_NOTIFY_OPERATOR,
            appUser,
            taskActionPayload
        );

        verify(validator, times(1)).validateCivilPenalty(taskPayload);
        verify(validator, times(1)).validateUsers(requestTask, operators, Set.of(), appUser);
        verify(validator, times(1)).validateContactAddress(request);
        verify(requestService, times(1)).addActionToRequest(
            request,
            actionPayload,
            RequestActionType.NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED,
            "userId"
        );
        verify(workflowService, times(1)).completeTask("processTaskId", Map.of(
            BpmnProcessConstants.NON_COMPLIANCE_OUTCOME, NonComplianceOutcome.SUBMITTED
        ));
        verify(officialNoticeServiceDelegator, times(1)).sendOfficialNotice(
            civilPenalty, request, decisionNotification
        );
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.NON_COMPLIANCE_CIVIL_PENALTY_NOTIFY_OPERATOR);
    }
}
