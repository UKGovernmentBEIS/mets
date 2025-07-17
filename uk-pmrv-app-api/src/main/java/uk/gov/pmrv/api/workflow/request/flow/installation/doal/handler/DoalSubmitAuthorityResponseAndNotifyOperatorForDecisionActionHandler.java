package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalAuthorityResponseRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalAuthorityResponseType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.service.DoalAuthorityResponseService;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation.DoalAuthorityResponseValidator;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DoalSubmitAuthorityResponseAndNotifyOperatorForDecisionActionHandler implements
        RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final DoalAuthorityResponseValidator doalAuthorityResponseValidator;
    private final DoalAuthorityResponseService doalAuthorityResponseService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload) {

        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final DoalAuthorityResponseRequestTaskPayload taskPayload =
                (DoalAuthorityResponseRequestTaskPayload) requestTask.getPayload();

        // Validate
        doalAuthorityResponseValidator.validate(requestTask, taskPayload.getDoalAuthority(),
                taskActionPayload.getDecisionNotification(), appUser);

        // Notify Operator
        doalAuthorityResponseService.authorityResponseNotifyOperator(requestTask, taskActionPayload);

        // Complete task
        DoalAuthorityResponseType authorityResponseType = taskPayload.getDoalAuthority().getAuthorityResponse().getType();
        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.DOAL_AUTHORITY_RESPONSE, authorityResponseType)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.DOAL_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
