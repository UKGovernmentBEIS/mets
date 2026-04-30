package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.bdrs2;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollectInstallationAccountsForBDRS2HandlerFlowableTest {

    @InjectMocks
    private CollectInstallationAccountsForBDRS2HandlerFlowable handler;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute_withoutAccountIds() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        List<Long> accounts = List.of(accountId1, accountId2);

        when(execution.hasVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(false);
        when(installationAccountQueryService.findEligibleAccountIdsForBDRS2()).thenReturn(accounts);

        handler.execute(execution);

        verify(execution, times(1)).setVariable("accounts", accounts);
    }

    @Test
    void execute_withAccountIds() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;

        when(execution.hasVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(true);
        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(List.of(accountId1.toString(), accountId2.toString()));
        when(installationAccountQueryService.existsAccountById(accountId1)).thenReturn(true);
        when(installationAccountQueryService.existsAccountById(accountId2)).thenReturn(false);

        handler.execute(execution);

        verify(execution, times(1)).setVariable("accounts", List.of(accountId1));
        verify(installationAccountQueryService, never()).findEligibleAccountIdsForBDRS2();
    }
}
