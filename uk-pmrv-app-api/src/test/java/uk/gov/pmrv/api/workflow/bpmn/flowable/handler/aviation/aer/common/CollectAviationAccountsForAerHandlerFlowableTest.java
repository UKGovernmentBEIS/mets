package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.aer.common;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollectAviationAccountsForAerHandlerFlowableTest {

    @InjectMocks
    private CollectAviationAccountsForAerHandlerFlowable collectAviationAccountsForAerHandler;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Mock
    private DelegateExecution execution;

    @Test
    void executeAutomaticWorkflow() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        List<Long> accounts = List.of(accountId1, accountId2);

        // No ACCOUNT_IDS variable → automatic mode
        when(execution.hasVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(false);

        when(aviationAccountQueryService.getAccountIdsByStatuses(
                List.of(AviationAccountStatus.NEW, AviationAccountStatus.LIVE)))
                .thenReturn(accounts);

        collectAviationAccountsForAerHandler.execute(execution);

        verify(execution, times(1)).setVariable("accounts", accounts);
    }

    @Test
    void executeManualWorkflow() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;

        // ACCOUNT_IDS exists → manual mode
        when(execution.hasVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(true);
        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_IDS))
                .thenReturn(List.of(accountId1.toString(), accountId2.toString()));

        when(aviationAccountQueryService.existsAccountById(accountId1)).thenReturn(true);
        when(aviationAccountQueryService.existsAccountById(accountId2)).thenReturn(false);

        collectAviationAccountsForAerHandler.execute(execution);

        verify(execution, times(1)).setVariable("accounts", List.of(accountId1));
        verify(aviationAccountQueryService, never()).getAccountIdsByStatuses(any());
    }
}
