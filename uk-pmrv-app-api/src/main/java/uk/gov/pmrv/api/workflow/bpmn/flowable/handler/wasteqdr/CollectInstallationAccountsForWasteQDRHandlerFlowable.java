package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.wasteqdr;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;

import java.util.List;

import static uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants.ACCOUNT_IDS;

@Service
@RequiredArgsConstructor
public class CollectInstallationAccountsForWasteQDRHandlerFlowable implements JavaDelegate {

    private final InstallationAccountQueryService installationAccountQueryService;

    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable("accounts", getInstallationAccounts(execution));
    }

    private List<Long> getInstallationAccounts(DelegateExecution execution) {
        if (!execution.hasVariable(ACCOUNT_IDS)) {
            return installationAccountQueryService.getAccountIdsByStatus(InstallationAccountStatus.LIVE)
                    .stream()
                    .filter(this::isEmitterTypeWaste)
                    .toList();
        }
        List<String> providedAccountIds = (List<String>) execution.getVariable(ACCOUNT_IDS);
        return getExistingInstallationAccounts(providedAccountIds);
    }

    private List<Long> getExistingInstallationAccounts(List<String> providedAccountIds) {
        return providedAccountIds
                .stream()
                .map(accountId -> Long.parseLong(accountId.trim()))
                .filter(installationAccountQueryService::existsAccountById)
                .filter(this::isEmitterTypeWaste)
                .toList();
    }

    private boolean isEmitterTypeWaste(Long accountId) {
        InstallationAccountDTO accountDTO = installationAccountQueryService.getAccountDTOById(accountId);
        return accountDTO != null
                && EmitterType.WASTE.equals(accountDTO.getEmitterType());
    }
}
