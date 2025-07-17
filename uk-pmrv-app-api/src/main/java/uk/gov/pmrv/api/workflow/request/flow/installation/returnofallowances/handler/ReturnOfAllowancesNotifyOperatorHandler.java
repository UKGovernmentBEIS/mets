package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service.ReturnOfAllowancesService;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.validator.ReturnOfAllowancesSubmissionValidator;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReturnOfAllowancesNotifyOperatorHandler
    implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final ReturnOfAllowancesService returnOfAllowancesService;
    private final ReturnOfAllowancesSubmissionValidator returnOfAllowancesSubmissionValidator;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        NotifyOperatorForDecisionRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        //validate
        returnOfAllowancesSubmissionValidator.validate(requestTask, payload, appUser);

        // save
        returnOfAllowancesService.saveReturnOfAllowancesDecisionNotification(payload, requestTask);

        // complete task
        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.RETURN_OF_ALLOWANCES_SUBMIT_OUTCOME,
                ReturnOfAllowancesSubmitOutcome.SUBMITTED)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.RETURN_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION);
    }

}
