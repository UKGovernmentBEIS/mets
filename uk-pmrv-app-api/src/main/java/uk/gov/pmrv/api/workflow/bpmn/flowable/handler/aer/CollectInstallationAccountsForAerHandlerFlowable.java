package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;

import java.util.List;

import static uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants.ACCOUNT_IDS;

/**
 * Collects account ids to initiate AER for: </br>
 *
 * <ul>
 *     <li>LIVE accounts when the associated timer in BPMN has been executed</li>
 *     <li>OR for the provided account ids through the BPMN REST API. It is useful when some AERs have not been successfully executed
 *     when the timer kicked in.</li>
 * </ul>
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class CollectInstallationAccountsForAerHandlerFlowable implements JavaDelegate {

    private final InstallationAccountQueryService installationAccountQueryService;

    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable("accounts", getInstallationAccounts(execution));
    }

    private List<Long> getInstallationAccounts(DelegateExecution execution) {
        if (!execution.hasVariable(ACCOUNT_IDS)) {
            return installationAccountQueryService.getAccountIdsByStatus(InstallationAccountStatus.LIVE);
        }
        List<String> providedAccountIds = (List<String>) execution.getVariable(ACCOUNT_IDS);
        return getExistingInstallationAccounts(providedAccountIds);
    }

    private List<Long> getExistingInstallationAccounts(List<String> providedAccountIds) {
        return providedAccountIds
            .stream()
            .map(accountId -> Long.parseLong(accountId.trim()))
            .filter(installationAccountQueryService::existsAccountById)
            .toList();
    }
}
