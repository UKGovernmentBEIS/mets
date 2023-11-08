package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.aer.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;

import java.util.List;

import static uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants.ACCOUNT_IDS;

/**
 * Collects account ids to initiate AER for: </br>
 *
 * <ul>
 *     <li>LIVE accounts when the associated timer in Camunda has been executed</li>
 *     <li>OR for the provided account ids through the Camunda REST API. It is useful when some AERs have not been successfully executed
 *     when the timer kicked in.</li>
 * </ul>
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class CollectAviationAccountsForAerHandler implements JavaDelegate {
    private final AviationAccountQueryService aviationAccountQueryService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        execution.setVariable("accounts", getAviationAccounts(execution));
    }

    private List<Long> getAviationAccounts(DelegateExecution execution) {
        if (!execution.getProcessInstance().hasVariable(ACCOUNT_IDS)) {
            return aviationAccountQueryService.getAccountIdsByStatuses(List.of(AviationAccountStatus.NEW, AviationAccountStatus.LIVE));
        }

        List<String> providedAccountIds = (List<String>) execution.getProcessInstance().getVariable(ACCOUNT_IDS);

        return getExistingAviationAccounts(providedAccountIds);
    }

    private List<Long> getExistingAviationAccounts(List<String> providedAccountIds) {
        return providedAccountIds
                .stream()
                .map(accountId -> Long.parseLong(accountId.trim()))
                .filter(aviationAccountQueryService::existsAccountById)
                .toList();
    }
}
