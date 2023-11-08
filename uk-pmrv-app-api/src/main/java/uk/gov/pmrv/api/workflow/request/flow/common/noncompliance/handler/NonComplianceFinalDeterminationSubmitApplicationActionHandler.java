package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceFinalDeterminationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceFinalDeterminationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.mapper.NonComplianceMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.validation.NonComplianceApplicationValidator;

@Component
@RequiredArgsConstructor
public class NonComplianceFinalDeterminationSubmitApplicationActionHandler
    implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

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
        final NonComplianceFinalDeterminationRequestTaskPayload taskPayload =
            (NonComplianceFinalDeterminationRequestTaskPayload) requestTask.getPayload();

        validator.validateFinalDetermination(taskPayload);

        final Request request = requestTask.getRequest();
        final NonComplianceFinalDeterminationApplicationSubmittedRequestActionPayload actionPayload =
            NON_COMPLIANCE_MAPPER.toFinalDeterminationSubmittedRequestAction(taskPayload);

        requestService.addActionToRequest(
            request,
            actionPayload,
            RequestActionType.NON_COMPLIANCE_FINAL_DETERMINATION_APPLICATION_SUBMITTED,
            pmrvUser.getUserId()
        );

        final boolean reissuePenalty = Boolean.TRUE.equals(taskPayload.getFinalDetermination().getReissuePenalty());

        final NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        requestPayload.setReIssueCivilPenalty(reissuePenalty);

        final NonComplianceOutcome
            outcome = reissuePenalty ? NonComplianceOutcome.REISSUE_CIVIL_PENALTY : NonComplianceOutcome.SUBMITTED; 
        workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
            BpmnProcessConstants.NON_COMPLIANCE_OUTCOME, outcome
        ));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.NON_COMPLIANCE_FINAL_DETERMINATION_SUBMIT_APPLICATION);
    }
}
