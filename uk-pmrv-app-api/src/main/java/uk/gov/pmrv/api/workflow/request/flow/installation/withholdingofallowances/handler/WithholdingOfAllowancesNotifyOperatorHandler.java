package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesService;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator.WithholdingOfAllowancesSubmissionValidator;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WithholdingOfAllowancesNotifyOperatorHandler
    implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final WithholdingOfAllowancesService withholdingOfAllowancesService;
    private final WithholdingOfAllowancesSubmissionValidator withholdingOfAllowancesSubmissionValidator;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        NotifyOperatorForDecisionRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        //validate
        withholdingOfAllowancesSubmissionValidator.validate(requestTask, payload, appUser);

        // save
        withholdingOfAllowancesService.saveWithholdingOfAllowancesDecisionNotification(payload, requestTask);

        // complete task
        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.WITHHOLDING_OF_ALLOWANCES_SUBMIT_OUTCOME,
                WithholdingOfAllowancesSubmitOutcome.SUBMITTED)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION);
    }

}
