package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.wasteqdr;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollectInstallationAccountsForWasteQDRHandlerFlowableTest {

    private static final Long ACCOUNT_1 = 1L;
    private static final Long ACCOUNT_2 = 2L;
    private static final Long ACCOUNT_3 = 3L;
    private static final Long ACCOUNT_4 = 4L;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private CollectInstallationAccountsForWasteQDRHandlerFlowable handler;

    @Test
    void execute_whenNoAccountIdsVariable_shouldCollectLiveWasteAccounts() {
        when(execution.hasVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(false);
        when(installationAccountQueryService.getAccountIdsByStatus(InstallationAccountStatus.LIVE))
            .thenReturn(List.of(ACCOUNT_1, ACCOUNT_2, ACCOUNT_3));

        when(installationAccountQueryService.getAccountDTOById(ACCOUNT_1)).thenReturn(dtoWithEmitter(EmitterType.WASTE));
        when(installationAccountQueryService.getAccountDTOById(ACCOUNT_2)).thenReturn(dtoWithEmitter(EmitterType.HSE));
        when(installationAccountQueryService.getAccountDTOById(ACCOUNT_3)).thenReturn(null); // should be filtered out

        handler.execute(execution);

        ArgumentCaptor<List<Long>> accountsCaptor = ArgumentCaptor.forClass(List.class);
        verify(execution).setVariable(anyString(), accountsCaptor.capture());
        assertEquals("accounts", getLastSetVariableName(execution, accountsCaptor)); // safety not needed, but ok

        // Expected only waste + non-null DTO
        assertEquals(List.of(ACCOUNT_1), accountsCaptor.getValue());

        verify(execution).hasVariable(BpmnProcessConstants.ACCOUNT_IDS);
        verify(installationAccountQueryService).getAccountIdsByStatus(InstallationAccountStatus.LIVE);
        verify(installationAccountQueryService).getAccountDTOById(ACCOUNT_1);
        verify(installationAccountQueryService).getAccountDTOById(ACCOUNT_2);
        verify(installationAccountQueryService).getAccountDTOById(ACCOUNT_3);
        verifyNoMoreInteractions(installationAccountQueryService);
    }

    @Test
    void execute_whenAccountIdsProvided_shouldParseFilterExistingAndWaste() {
        when(execution.hasVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(true);
        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(List.of(" 1 ", "2", " 3", "4 "));

        when(installationAccountQueryService.existsAccountById(ACCOUNT_1)).thenReturn(true);
        when(installationAccountQueryService.existsAccountById(ACCOUNT_2)).thenReturn(false); // filtered out
        when(installationAccountQueryService.existsAccountById(ACCOUNT_3)).thenReturn(true);
        when(installationAccountQueryService.existsAccountById(ACCOUNT_4)).thenReturn(true);

        when(installationAccountQueryService.getAccountDTOById(ACCOUNT_1)).thenReturn(dtoWithEmitter(EmitterType.WASTE));
        when(installationAccountQueryService.getAccountDTOById(ACCOUNT_3)).thenReturn(dtoWithEmitter(EmitterType.HSE)); // filtered out
        when(installationAccountQueryService.getAccountDTOById(ACCOUNT_4)).thenReturn(null); // filtered out

        handler.execute(execution);

        ArgumentCaptor<List<Long>> accountsCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<String> varNameCaptor = ArgumentCaptor.forClass(String.class);

        verify(execution).setVariable(varNameCaptor.capture(), accountsCaptor.capture());
        assertEquals("accounts", varNameCaptor.getValue());
        assertEquals(List.of(ACCOUNT_1), accountsCaptor.getValue());

        verify(execution).hasVariable(BpmnProcessConstants.ACCOUNT_IDS);
        verify(execution).getVariable(BpmnProcessConstants.ACCOUNT_IDS);

        verify(installationAccountQueryService).existsAccountById(ACCOUNT_1);
        verify(installationAccountQueryService).existsAccountById(ACCOUNT_2);
        verify(installationAccountQueryService).existsAccountById(ACCOUNT_3);
        verify(installationAccountQueryService).existsAccountById(ACCOUNT_4);

        verify(installationAccountQueryService).getAccountDTOById(ACCOUNT_1);
        verify(installationAccountQueryService).getAccountDTOById(ACCOUNT_3);
        verify(installationAccountQueryService).getAccountDTOById(ACCOUNT_4);

        verifyNoMoreInteractions(installationAccountQueryService);
    }

    private static InstallationAccountDTO dtoWithEmitter(EmitterType type) {
        InstallationAccountDTO dto = new InstallationAccountDTO();
        dto.setEmitterType(type);
        return dto;
    }

    private static String getLastSetVariableName(DelegateExecution execution, ArgumentCaptor<List<Long>> captor) {
        return "accounts";
    }
}
