package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.bdrs2;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;

import java.util.List;

import static uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants.ACCOUNT_IDS;

@Service
@RequiredArgsConstructor
public class CollectInstallationAccountsForBDRS2HandlerFlowable implements JavaDelegate {

    private final InstallationAccountQueryService installationAccountQueryService;

    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable("accounts", getInstallationAccounts(execution));
    }

    private List<Long> getInstallationAccounts(DelegateExecution execution) {
        if (!execution.hasVariable(ACCOUNT_IDS)) {
            return installationAccountQueryService.findEligibleAccountIdsForBDRS2();
        }
        @SuppressWarnings("unchecked")
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
