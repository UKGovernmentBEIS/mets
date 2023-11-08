package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltyRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceNotifyOperatorRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.mapper.NonComplianceMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service.NonComplianceSendOfficialNoticeServiceDelegator;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.validation.NonComplianceApplicationValidator;

@Component
@RequiredArgsConstructor
public class NonComplianceCivilPenaltyNotifyOperatorActionHandler
    implements RequestTaskActionHandler<NonComplianceNotifyOperatorRequestTaskActionPayload> {

    private static final NonComplianceMapper NON_COMPLIANCE_MAPPER = Mappers.getMapper(NonComplianceMapper.class);

    private final RequestTaskService requestTaskService;
    private final NonComplianceApplicationValidator validator;
    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private final WorkflowService workflowService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final NonComplianceSendOfficialNoticeServiceDelegator officialNoticeServiceDelegator;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final NonComplianceNotifyOperatorRequestTaskActionPayload taskActionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final NonComplianceCivilPenaltyRequestTaskPayload taskPayload =
            (NonComplianceCivilPenaltyRequestTaskPayload) requestTask.getPayload();
        final NonComplianceDecisionNotification decisionNotification = taskActionPayload.getDecisionNotification();
        final Set<String> operators = decisionNotification.getOperators();
        final Set<Long> externalContacts = decisionNotification.getExternalContacts();
        final Request request = requestTask.getRequest();

        // validate
        validator.validateCivilPenalty(taskPayload);
        validator.validateUsers(requestTask, operators, externalContacts, pmrvUser);
        validator.validateContactAddress(request);

        // add timeline action
        Optional<UserInfoDTO> requestAccountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request);
        final Map<String, RequestActionUserInfo> usersInfo = requestAccountPrimaryContact.isPresent() ?
            requestActionUserInfoResolver.getUsersInfo(operators, request) : null;
        final NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload actionPayload =
            NON_COMPLIANCE_MAPPER.toCivilPenaltySubmittedRequestAction(taskPayload, decisionNotification, usersInfo);

        requestService.addActionToRequest(
            request,
            actionPayload,
            RequestActionType.NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED,
            pmrvUser.getUserId()
        );

        // complete task
        workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
            BpmnProcessConstants.NON_COMPLIANCE_OUTCOME, NonComplianceOutcome.SUBMITTED
        ));

        // send email
        officialNoticeServiceDelegator.sendOfficialNotice(taskPayload.getCivilPenalty(), request, decisionNotification);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.NON_COMPLIANCE_CIVIL_PENALTY_NOTIFY_OPERATOR);
    }
}
