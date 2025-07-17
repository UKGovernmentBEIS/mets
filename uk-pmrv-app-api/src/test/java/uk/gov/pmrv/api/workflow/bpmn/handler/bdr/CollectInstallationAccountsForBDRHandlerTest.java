package uk.gov.pmrv.api.workflow.bpmn.handler.bdr;


import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CollectInstallationAccountsForBDRHandlerTest {

    @InjectMocks
    private CollectInstallationAccountsForBDRHandler handler;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;


    @Mock
    private DelegateExecution execution;

    @Test
    void execute_withoutAccountIds() throws Exception {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        List<Long> accounts = List.of(accountId1, accountId2);

        when(execution.getProcessInstance()).thenReturn(execution);

        when(execution.hasVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(false);
        when(installationAccountQueryService.getAccountIdsByStatus(InstallationAccountStatus.LIVE)).thenReturn(accounts);

        handler.execute(execution);

        verify(execution, times(1)).setVariable("accounts", accounts);
    }

    @Test
    void execute_withAccountIds() throws Exception {
        Long accountId1 = 1L;
        Long accountId2 = 2L;

        when(execution.getProcessInstance()).thenReturn(execution);

        when(execution.hasVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(true);
        when(execution.getProcessInstance().getVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(List.of(accountId1.toString(), accountId2.toString()));
        when(installationAccountQueryService.existsAccountById(accountId1)).thenReturn(true);
        when(installationAccountQueryService.existsAccountById(accountId2)).thenReturn(false);

        handler.execute(execution);

        verify(execution, times(1)).setVariable("accounts", List.of(accountId1));
        verify(installationAccountQueryService, never()).getAccountIdsByStatus(any());
    }
}
