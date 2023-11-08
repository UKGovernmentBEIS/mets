package uk.gov.pmrv.api.workflow.bpmn.handler.aer;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.once;

@ExtendWith(MockitoExtension.class)
class CollectInstallationAccountsForAerHandlerTest {
    @InjectMocks
    private CollectInstallationAccountsForAerHandler collectInstallationAccountsForAerHandler;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private DelegateExecution execution;

    @Test
    void executeAutomaticWorkflow() throws Exception {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        List<Long> accounts = List.of(accountId1, accountId2);

        DelegateExecution delegateExecution = mock(DelegateExecution.class);
        when(execution.getProcessInstance()).thenReturn(delegateExecution);

        when(delegateExecution.hasVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(false);
        when(installationAccountQueryService.getAccountIdsByStatus(InstallationAccountStatus.LIVE)).thenReturn(accounts);

        // Invoke
        collectInstallationAccountsForAerHandler.execute(execution);

        verify(execution, times(1)).setVariable("accounts", accounts);
    }

    @Test
    void executeManualWorkflow() throws Exception {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        List<Long> accounts = List.of(accountId1, accountId2);

        DelegateExecution delegateExecution = mock(DelegateExecution.class);
        when(execution.getProcessInstance()).thenReturn(delegateExecution);

        when(delegateExecution.hasVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(true);
        when(execution.getProcessInstance().getVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(List.of(accountId1.toString(), accountId2.toString()));
        when(installationAccountQueryService.existsAccountById(accountId1)).thenReturn(true);
        when(installationAccountQueryService.existsAccountById(accountId2)).thenReturn(false);


        // Invoke
        collectInstallationAccountsForAerHandler.execute(execution);

        verify(execution, times(1)).setVariable("accounts",  List.of(accountId1));
        verify(installationAccountQueryService, never()).getAccountIdsByStatus(any());
    }
}