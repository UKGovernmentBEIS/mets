package uk.gov.pmrv.api.workflow.bpmn.handler.alr;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class CollectInstallationAccountsForALRHandlerTest {

    @InjectMocks
    private CollectInstallationAccountsForALRHandler handler;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private DelegateExecution execution;


    @Test
    void execute_withoutAccountIds() throws Exception {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        List<Long> accounts = List.of(accountId1, accountId2);
        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .faStatus(true)
                .emitterType(EmitterType.GHGE)
                .build();

        when(execution.getProcessInstance()).thenReturn(execution);
        when(execution.hasVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(false);
        when(installationAccountQueryService.getAccountIdsByStatus(InstallationAccountStatus.LIVE)).thenReturn(accounts);
        when(installationAccountQueryService.getAccountDTOById(any())).thenReturn(accountDTO);

        handler.execute(execution);

        verify(execution, times(1)).setVariable("accounts", accounts);
    }

    @Test
    void execute_withAccountIds() throws Exception {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        Long accountId3 = 3L;

        InstallationAccountDTO accountDTO1 = InstallationAccountDTO.builder()
                .faStatus(true)
                .emitterType(EmitterType.GHGE)
                .build();

        InstallationAccountDTO accountDTO2 = InstallationAccountDTO.builder()
                .faStatus(false)
                .emitterType(EmitterType.GHGE)
                .build();

        when(execution.getProcessInstance()).thenReturn(execution);
        when(execution.hasVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(true);
        when(execution.getProcessInstance().getVariable(BpmnProcessConstants.ACCOUNT_IDS))
                .thenReturn(List.of(accountId1.toString(), accountId2.toString(), accountId3.toString()));
        when(installationAccountQueryService.existsAccountById(accountId1)).thenReturn(true);
        when(installationAccountQueryService.existsAccountById(accountId2)).thenReturn(false);
        when(installationAccountQueryService.existsAccountById(accountId3)).thenReturn(true);
        when(installationAccountQueryService.getAccountDTOById(accountId1)).thenReturn(accountDTO1);
        when(installationAccountQueryService.getAccountDTOById(accountId3)).thenReturn(accountDTO2);


        handler.execute(execution);

        verify(execution, times(1)).setVariable("accounts", List.of(accountId1));
        verify(installationAccountQueryService, never()).getAccountIdsByStatus(any());
    }
}
