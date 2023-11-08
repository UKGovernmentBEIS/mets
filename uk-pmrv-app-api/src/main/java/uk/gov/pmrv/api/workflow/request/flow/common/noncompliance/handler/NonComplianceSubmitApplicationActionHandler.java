package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.mapper.NonComplianceMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.validation.NonComplianceApplicationValidator;

@Component
@RequiredArgsConstructor
public class NonComplianceSubmitApplicationActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private static final NonComplianceMapper NON_COMPLIANCE_MAPPER = Mappers.getMapper(NonComplianceMapper.class);
    
    private final RequestTaskService requestTaskService;
    private final NonComplianceApplicationValidator validator;
    private final WorkflowService workflowService;
    private final RequestService requestService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final RequestTaskActionEmptyPayload taskActionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final NonComplianceApplicationSubmitRequestTaskPayload taskPayload =
            (NonComplianceApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        final Boolean civilPenalty = taskPayload.getNonCompliancePenalties().getCivilPenalty();
        final Boolean dailyPenalty = taskPayload.getNonCompliancePenalties().getDailyPenalty();
        final Boolean noticeOfIntent = taskPayload.getNonCompliancePenalties().getNoticeOfIntent();

        validator.validateApplication(taskPayload);

        final Request request = requestTask.getRequest();
        final NonComplianceApplicationSubmittedRequestActionPayload actionPayload =
            NON_COMPLIANCE_MAPPER.toSubmittedRequestAction(taskPayload);

        request.setSubmissionDate(LocalDateTime.now());
        final NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        requestPayload.setIssueNoticeOfIntent(Boolean.TRUE.equals(noticeOfIntent));

        requestService.addActionToRequest(
            request,
            actionPayload,
            RequestActionType.NON_COMPLIANCE_APPLICATION_SUBMITTED,
            pmrvUser.getUserId()
        );

        final Map<String, Object> variables = new HashMap<>();
        variables.put(BpmnProcessConstants.NON_COMPLIANCE_OUTCOME, NonComplianceOutcome.SUBMITTED);
        variables.put(BpmnProcessConstants.CIVIL_PENALTY_LIABLE, civilPenalty);
        if (Boolean.TRUE.equals(civilPenalty)) {
            variables.put(BpmnProcessConstants.DAILY_PENALTY_LIABLE, dailyPenalty);
            variables.put(BpmnProcessConstants.NOI_PENALTY_LIABLE, noticeOfIntent);
        }
        
        workflowService.completeTask(requestTask.getProcessTaskId(), variables);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.NON_COMPLIANCE_SUBMIT_APPLICATION);
    }
}
