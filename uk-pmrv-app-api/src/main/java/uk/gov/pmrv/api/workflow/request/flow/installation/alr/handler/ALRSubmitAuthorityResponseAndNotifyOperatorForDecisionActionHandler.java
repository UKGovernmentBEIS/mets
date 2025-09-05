package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAuthorityResponseSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRAuthorityResponseService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation.ALRAuthorityResponseValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalAuthorityResponseType;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ALRSubmitAuthorityResponseAndNotifyOperatorForDecisionActionHandler implements
        RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final ALRAuthorityResponseValidator alrAuthorityResponseValidator;
    private final ALRAuthorityResponseService alrAuthorityResponseService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload) {

        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final ALRAuthorityResponseSubmitRequestTaskPayload taskPayload =
                (ALRAuthorityResponseSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate
        alrAuthorityResponseValidator.validate(requestTask, taskPayload.getAuthorityReviewOutcome(),
                taskActionPayload.getDecisionNotification(), appUser);

        // Notify Operator
        alrAuthorityResponseService.authorityResponseNotifyOperator(requestTask, taskActionPayload);

        // Complete task
        DoalAuthorityResponseType authorityResponseType = taskPayload.getAuthorityReviewOutcome().getAuthorityResponse().getType();
        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.ALR_AUTHORITY_RESPONSE, authorityResponseType)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.ALR_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
