package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.alr;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants.ACCOUNT_IDS;

@Service
@RequiredArgsConstructor
public class CollectInstallationAccountsForALRHandlerFlowable implements JavaDelegate {


    private final InstallationAccountQueryService installationAccountQueryService;

    @Override
    public void execute(DelegateExecution execution) {
        execution.setVariable("accounts", getInstallationAccounts(execution));
        execution.setVariable(BpmnProcessConstants.ALR_FINAL, Boolean.FALSE);
    }

    private List<Long> getInstallationAccounts(DelegateExecution execution) {
        if (!execution.hasVariable(ACCOUNT_IDS)) {
            return installationAccountQueryService.getAccountIdsByStatus(InstallationAccountStatus.LIVE)
                    .stream()
                    .filter(this::isEmitterTypeGHGEAndFaTrue)
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
                .filter(this::isEmitterTypeGHGEAndFaTrue)
                .toList();
    }

    private boolean isEmitterTypeGHGEAndFaTrue(Long accountId) {
        InstallationAccountDTO accountDTO = installationAccountQueryService.getAccountDTOById(accountId);
        return accountDTO != null
                && EmitterType.GHGE.equals(accountDTO.getEmitterType())
                && Boolean.TRUE.equals(accountDTO.getFaStatus());
    }
}
