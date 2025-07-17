package uk.gov.pmrv.api.workflow.bpmn.handler.bdr;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;

import java.util.List;

import static uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants.ACCOUNT_IDS;

@Service
@RequiredArgsConstructor
public class CollectInstallationAccountsForBDRHandler implements JavaDelegate {

    private final InstallationAccountQueryService installationAccountQueryService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        execution.setVariable("accounts", getInstallationAccounts(execution));
    }

    private List<Long> getInstallationAccounts(DelegateExecution execution) {
        if (!execution.getProcessInstance().hasVariable(ACCOUNT_IDS)) {
            return installationAccountQueryService.getAccountIdsByStatus(InstallationAccountStatus.LIVE);
        }
        List<String> providedAccountIds = (List<String>) execution.getProcessInstance().getVariable(ACCOUNT_IDS);
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
