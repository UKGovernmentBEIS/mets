package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalCloseApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesWithdrawalService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WithholdingOfAllowanceWithdrawalCloseActionHandler
    implements RequestTaskActionHandler<WithholdingOfAllowancesWithdrawalCloseApplicationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final WithholdingOfAllowancesWithdrawalService withholdingOfAllowancesWithdrawalService;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final WithholdingOfAllowancesWithdrawalCloseApplicationRequestTaskActionPayload taskActionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        withholdingOfAllowancesWithdrawalService.applyCloseAction(requestTask, taskActionPayload);
        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(
                BpmnProcessConstants.WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_OUTCOME,
                WithholdingOfAllowancesWithdrawalOutcome.CLOSED
            )
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_CLOSE_APPLICATION);
    }
}
