package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.aer.common;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.bpmn.handler.aer.CollectInstallationAccountsForAerHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollectAviationAccountsForAerHandlerTest {

    @InjectMocks
    private CollectAviationAccountsForAerHandler collectAviationAccountsForAerHandler;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

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
        when(aviationAccountQueryService.getAccountIdsByStatuses(List.of(AviationAccountStatus.NEW, AviationAccountStatus.LIVE))).thenReturn(accounts);

        // Invoke
        collectAviationAccountsForAerHandler.execute(execution);

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
        when(aviationAccountQueryService.existsAccountById(accountId1)).thenReturn(true);
        when(aviationAccountQueryService.existsAccountById(accountId2)).thenReturn(false);


        // Invoke
        collectAviationAccountsForAerHandler.execute(execution);

        verify(execution, times(1)).setVariable("accounts",  List.of(accountId1));
        verify(aviationAccountQueryService, never()).getAccountIdsByStatuses(any());
    }

}