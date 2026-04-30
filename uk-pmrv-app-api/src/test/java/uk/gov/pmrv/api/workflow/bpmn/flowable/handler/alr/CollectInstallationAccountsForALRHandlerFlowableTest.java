package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.alr;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants.ACCOUNT_IDS;

@ExtendWith(MockitoExtension.class)
class CollectInstallationAccountsForALRHandlerFlowableTest {

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private CollectInstallationAccountsForALRHandlerFlowable handler;

    @Captor
    private ArgumentCaptor<List<Long>> accountsCaptor;

    @Test
    void execute_whenNoAccountIdsVariable_collectsLiveAccountsAndFiltersByGhgeAndFaTrue() {
        when(execution.hasVariable(ACCOUNT_IDS)).thenReturn(false);

        when(installationAccountQueryService.getAccountIdsByStatus(InstallationAccountStatus.LIVE))
                .thenReturn(List.of(1L, 2L, 3L, 4L));

        // 1 -> GHGE + true (keep)
        when(installationAccountQueryService.getAccountDTOById(1L))
                .thenReturn(dto(EmitterType.GHGE, true));

        // 2 -> GHGE + false (drop)
        when(installationAccountQueryService.getAccountDTOById(2L))
                .thenReturn(dto(EmitterType.GHGE, false));

        // 3 -> not GHGE + true (drop)
        when(installationAccountQueryService.getAccountDTOById(3L))
                .thenReturn(dto(EmitterType.HSE, true));

        // 4 -> null DTO (drop)
        when(installationAccountQueryService.getAccountDTOById(4L))
                .thenReturn(null);

        handler.execute(execution);

        verify(execution).setVariable(eq("accounts"), accountsCaptor.capture());
        assertThat(accountsCaptor.getValue()).containsExactly(1L);

        verify(execution).setVariable(eq(BpmnProcessConstants.ALR_FINAL), eq(Boolean.FALSE));

        verify(installationAccountQueryService).getAccountIdsByStatus(InstallationAccountStatus.LIVE);
        verify(installationAccountQueryService, never()).existsAccountById(anyLong());
    }

    @Test
    void execute_whenAccountIdsProvided_trimsParsesFiltersByExistsAndGhgeAndFaTrue() {
        when(execution.hasVariable(ACCOUNT_IDS)).thenReturn(true);
        when(execution.getVariable(ACCOUNT_IDS)).thenReturn(List.of(" 10 ", "11", " 12"));

        when(installationAccountQueryService.existsAccountById(10L)).thenReturn(true);
        when(installationAccountQueryService.existsAccountById(11L)).thenReturn(false); // drop before DTO lookup
        when(installationAccountQueryService.existsAccountById(12L)).thenReturn(true);

        when(installationAccountQueryService.getAccountDTOById(10L))
                .thenReturn(dto(EmitterType.GHGE, true));   // keep
        when(installationAccountQueryService.getAccountDTOById(12L))
                .thenReturn(dto(EmitterType.GHGE, false));  // drop

        handler.execute(execution);

        verify(execution).setVariable(eq("accounts"), accountsCaptor.capture());
        assertThat(accountsCaptor.getValue()).containsExactly(10L);

        verify(execution).setVariable(eq(BpmnProcessConstants.ALR_FINAL), eq(Boolean.FALSE));

        verify(installationAccountQueryService, never()).getAccountIdsByStatus(any());

        verify(installationAccountQueryService).existsAccountById(10L);
        verify(installationAccountQueryService).existsAccountById(11L);
        verify(installationAccountQueryService).existsAccountById(12L);

        // 11 doesn't exist -> should not call getAccountDTOById(11)
        verify(installationAccountQueryService, never()).getAccountDTOById(11L);
        verify(installationAccountQueryService).getAccountDTOById(10L);
        verify(installationAccountQueryService).getAccountDTOById(12L);
    }

    @Test
    void execute_filtersOutWhenDtoNullOrEmitterNotGhgeOrFaNotTrue() {
        when(execution.hasVariable(ACCOUNT_IDS)).thenReturn(false);
        when(installationAccountQueryService.getAccountIdsByStatus(InstallationAccountStatus.LIVE))
                .thenReturn(List.of(21L, 22L, 23L));

        when(installationAccountQueryService.getAccountDTOById(21L)).thenReturn(null);                       // drop
        when(installationAccountQueryService.getAccountDTOById(22L)).thenReturn(dto(EmitterType.HSE, true));  // drop
        when(installationAccountQueryService.getAccountDTOById(23L)).thenReturn(dto(EmitterType.GHGE, false)); // drop

        handler.execute(execution);

        verify(execution).setVariable(eq("accounts"), accountsCaptor.capture());
        assertThat(accountsCaptor.getValue()).isEmpty();

        verify(execution).setVariable(eq(BpmnProcessConstants.ALR_FINAL), eq(Boolean.FALSE));
    }

    private static InstallationAccountDTO dto(EmitterType emitterType, Boolean faStatus) {
        InstallationAccountDTO dto = new InstallationAccountDTO();
        dto.setEmitterType(emitterType);
        dto.setFaStatus(faStatus);
        return dto;
    }
}
