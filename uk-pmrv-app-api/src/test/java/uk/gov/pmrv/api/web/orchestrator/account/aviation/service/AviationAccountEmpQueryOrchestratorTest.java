package uk.gov.pmrv.api.web.orchestrator.account.aviation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpDetailsDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.dto.AviationAccountEmpDTO;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.dto.AviationAccountHeaderInfoDTO;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAccountEmpQueryOrchestratorTest {

    @InjectMocks
    private AviationAccountEmpQueryOrchestrator orchestrator;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Mock
    private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;

    @Test
    void getAviationAccountWithEMP() {
        final Long accountId = 1L;
        AppUser appUser = AppUser.builder().build();
        AviationAccountDTO aviationAccount = AviationAccountDTO.builder().build();
        EmpDetailsDTO empDetailsDTO = EmpDetailsDTO.builder().build();

        when(aviationAccountQueryService.getAviationAccountDTOByIdAndUser(accountId, appUser)).thenReturn(aviationAccount);
        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanDetailsDTOByAccountId(accountId)).thenReturn(Optional.of(empDetailsDTO));

        final AviationAccountEmpDTO aviationAccountWithEMP = orchestrator.getAviationAccountWithEMP(accountId, appUser);

        assertThat(aviationAccountWithEMP).isEqualTo(AviationAccountEmpDTO.builder()
                .aviationAccount(aviationAccount)
                .emp(empDetailsDTO)
                .build());

        verify(aviationAccountQueryService, times(1)).getAviationAccountDTOByIdAndUser(accountId, appUser);
        verify(emissionsMonitoringPlanQueryService, times(1)).getEmissionsMonitoringPlanDetailsDTOByAccountId(accountId);
    }

    @Test
    void getAviationAccountWithEMP_No_EMP() {
        final Long accountId = 1L;
        AviationAccountDTO aviationAccount = AviationAccountDTO.builder().build();
        AppUser appUser = AppUser.builder().build();

        when(aviationAccountQueryService.getAviationAccountDTOByIdAndUser(accountId, appUser)).thenReturn(aviationAccount);
        when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanDetailsDTOByAccountId(accountId)).thenReturn(Optional.empty());

        final AviationAccountEmpDTO aviationAccountWithEMP = orchestrator.getAviationAccountWithEMP(accountId, appUser);

        assertThat(aviationAccountWithEMP).isEqualTo(AviationAccountEmpDTO.builder()
                .aviationAccount(aviationAccount)
                .emp(null)
                .build());

        verify(aviationAccountQueryService, times(1)).getAviationAccountDTOByIdAndUser(accountId, appUser);
        verify(emissionsMonitoringPlanQueryService, times(1)).getEmissionsMonitoringPlanDetailsDTOByAccountId(accountId);
    }

    @Test
    void getAccountHeaderInfoWithEmpId() {
        Long accountId = 1L;
        String name = "name";
        EmissionTradingScheme scheme = EmissionTradingScheme.UK_ETS_AVIATION;
        AviationAccountStatus status = AviationAccountStatus.LIVE;
        String empId = "empId";

        AviationAccountInfoDTO accountInfo = AviationAccountInfoDTO.builder()
            .id(accountId)
            .name(name)
            .status(status)
            .emissionTradingScheme(scheme)
            .build();

        when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(accountInfo);
        when(emissionsMonitoringPlanQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.of(empId));

        AviationAccountHeaderInfoDTO expected = AviationAccountHeaderInfoDTO.builder()
            .id(accountId)
            .name(name)
            .status(status)
            .emissionTradingScheme(scheme)
            .empId(empId)
            .build();

        AviationAccountHeaderInfoDTO result = orchestrator.getAccountHeaderInfo(accountId);

        assertEquals(expected, result);

    }

    @Test
    void getAccountHeaderInfo_when_emp_exists() {
        Long accountId = 1L;
        String name = "name";
        EmissionTradingScheme scheme = EmissionTradingScheme.UK_ETS_AVIATION;
        AviationAccountStatus status = AviationAccountStatus.LIVE;
        String empId = "empId";

        AviationAccountInfoDTO accountInfo = AviationAccountInfoDTO.builder()
            .name(name)
            .status(status)
            .emissionTradingScheme(scheme)
            .build();

        when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(accountInfo);
        when(emissionsMonitoringPlanQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.of(empId));

        AviationAccountHeaderInfoDTO expected = AviationAccountHeaderInfoDTO.builder()
            .name(name)
            .status(status)
            .emissionTradingScheme(scheme)
            .empId(empId)
            .build();

        AviationAccountHeaderInfoDTO result = orchestrator.getAccountHeaderInfo(accountId);

        assertEquals(expected, result);
    }

    @Test
    void getAccountHeaderInfo_when_emp_not_exists() {
        Long accountId = 1L;
        String name = "name";
        EmissionTradingScheme scheme = EmissionTradingScheme.UK_ETS_AVIATION;
        AviationAccountStatus status = AviationAccountStatus.LIVE;

        AviationAccountInfoDTO accountInfo = AviationAccountInfoDTO.builder()
            .name(name)
            .status(status)
            .emissionTradingScheme(scheme)
            .build();

        when(aviationAccountQueryService.getAviationAccountInfoDTOById(accountId)).thenReturn(accountInfo);
        when(emissionsMonitoringPlanQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.empty());

        AviationAccountHeaderInfoDTO expected = AviationAccountHeaderInfoDTO.builder()
            .name(name)
            .status(status)
            .emissionTradingScheme(scheme)
            .build();

        AviationAccountHeaderInfoDTO result = orchestrator.getAccountHeaderInfo(accountId);

        assertEquals(expected, result);
    }

}
