package uk.gov.pmrv.api.web.orchestrator.account.installation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateRegistryReportingFirstYearDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountUpdateService;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.integration.registry.accountupdated.installation.request.InstallationAccountUpdatedRegistryEvent;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.reporting.domain.ReportableEmissionsEntity;
import uk.gov.pmrv.api.reporting.service.ReportableEmissionsService;

import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountCommandOrchestratorTest {

    @Mock
    private PermitQueryService permitQueryService;

    @Mock
    private InstallationAccountUpdateService installationAccountUpdateService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private ReportableEmissionsService reportableEmissionsService;

    @InjectMocks
    private InstallationAccountCommandOrchestrator orchestrator;

    @Test
    void updateRegistryReportingFirstYear_whenPermitContainerIsNull_shouldUpdateSuccessfully() {

        Long accountId = 1L;
        AccountUpdateRegistryReportingFirstYearDTO dto = new AccountUpdateRegistryReportingFirstYearDTO();
        InstallationAccountUpdatedRegistryEvent event = InstallationAccountUpdatedRegistryEvent.builder().accountId(accountId).skipRequestAction(true).build();
        dto.setRegistryReportingFirstYear(2024);

        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(null);
        when(installationAccountQueryService.getAccountDTOById(1L))
                .thenReturn(InstallationAccountDTO.builder().status(InstallationAccountStatus.LIVE).build());

        orchestrator.updateRegistryReportingFirstYear(accountId, dto);

        verify(permitQueryService, times(1)).getPermitContainerByAccountId(accountId);
        verify(installationAccountUpdateService, times(1)).updateRegistryReportingFirstYear(accountId, dto);
        verify(applicationEventPublisher, times(1)).publishEvent(event);

    }

    @Test
    void updateRegistryReportingFirstYear_whenPermitTypeIsGHGEAndYearIsNull_shouldThrowBusinessException() {

        Long accountId = 1L;
        AccountUpdateRegistryReportingFirstYearDTO dto = new AccountUpdateRegistryReportingFirstYearDTO();
        dto.setRegistryReportingFirstYear(null);

        PermitContainer permitContainer = new PermitContainer();
        permitContainer.setPermitType(PermitType.GHGE);

        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> orchestrator.updateRegistryReportingFirstYear(accountId, dto));

        assertEquals(MetsErrorCode.GHGE_REGISTRY_REPORTING_FIRST_YEAR_EMPTY_VALUE, exception.getErrorCode());
        verify(permitQueryService, times(1)).getPermitContainerByAccountId(accountId);
        verify(installationAccountUpdateService, never()).updateRegistryReportingFirstYear(accountId, dto);
    }

    @Test
    void updateRegistryReportingFirstYear_whenPermitTypeIsGHGEAndYearIsProvided_shouldUpdateSuccessfully() {

        Long accountId = 1L;
        AccountUpdateRegistryReportingFirstYearDTO dto = new AccountUpdateRegistryReportingFirstYearDTO();
        dto.setRegistryReportingFirstYear(2024);

        PermitContainer permitContainer = new PermitContainer();
        permitContainer.setPermitType(PermitType.GHGE);

        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
        when(installationAccountQueryService.getAccountDTOById(1L))
                .thenReturn(InstallationAccountDTO.builder().status(InstallationAccountStatus.LIVE).build());

        orchestrator.updateRegistryReportingFirstYear(accountId, dto);

        verify(permitQueryService, times(1)).getPermitContainerByAccountId(accountId);
        verify(installationAccountUpdateService, times(1)).updateRegistryReportingFirstYear(accountId, dto);
    }

    @Test
    void updateRegistryReportingFirstYear_whenPermitTypeIsNotGHGEAndYearIsNull_shouldUpdateSuccessfully() {

        Long accountId = 1L;
        AccountUpdateRegistryReportingFirstYearDTO dto = new AccountUpdateRegistryReportingFirstYearDTO();
        dto.setRegistryReportingFirstYear(null);

        PermitContainer permitContainer = new PermitContainer();
        permitContainer.setPermitType(PermitType.HSE); // or any other type

        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
        when(installationAccountQueryService.getAccountDTOById(1L))
                .thenReturn(InstallationAccountDTO.builder().status(InstallationAccountStatus.LIVE).build());

        orchestrator.updateRegistryReportingFirstYear(accountId, dto);

        verify(permitQueryService, times(1)).getPermitContainerByAccountId(accountId);
        verify(installationAccountUpdateService, times(1)).updateRegistryReportingFirstYear(accountId, dto);
    }

    @Test
    void updateRegistryReportingFirstYear_whenPermitTypeIsNotGHGEAndYearIsProvided_shouldUpdateSuccessfully() {

        Long accountId = 1L;
        AccountUpdateRegistryReportingFirstYearDTO dto = new AccountUpdateRegistryReportingFirstYearDTO();
        dto.setRegistryReportingFirstYear(2024);

        PermitContainer permitContainer = new PermitContainer();
        permitContainer.setPermitType(PermitType.HSE);

        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
        when(installationAccountQueryService.getAccountDTOById(1L))
                .thenReturn(InstallationAccountDTO.builder().status(InstallationAccountStatus.LIVE).build());

        orchestrator.updateRegistryReportingFirstYear(accountId, dto);

        verify(permitQueryService, times(1)).getPermitContainerByAccountId(accountId);
        verify(installationAccountUpdateService, times(1)).updateRegistryReportingFirstYear(accountId, dto);
    }

    @Test
    void updateRegistryReportingFirstYear_whenAccountStatusIsInvalid_shouldThrowBusinessException() {

        Long accountId = 1L;
        AccountUpdateRegistryReportingFirstYearDTO dto = new AccountUpdateRegistryReportingFirstYearDTO();
        dto.setRegistryReportingFirstYear(2025);

        PermitContainer permitContainer = new PermitContainer();
        permitContainer.setPermitType(PermitType.GHGE);

        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
        when(installationAccountQueryService.getAccountDTOById(1L))
                .thenReturn(InstallationAccountDTO.builder().status(InstallationAccountStatus.SURRENDERED).build());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> orchestrator.updateRegistryReportingFirstYear(accountId, dto));

        assertEquals(MetsErrorCode.REGISTRY_REPORTING_FIRST_YEAR_INVALID_ACCOUNT_STATUS, exception.getErrorCode());
        verify(permitQueryService, times(1)).getPermitContainerByAccountId(accountId);
        verify(installationAccountUpdateService, never()).updateRegistryReportingFirstYear(accountId, dto);
    }

    @Test
    void updateRegistryReportingFirstYear_whenEmissionsListIsEmpty_shouldUpdateSuccessfully() {

        Long accountId = 1L;
        AccountUpdateRegistryReportingFirstYearDTO dto = new AccountUpdateRegistryReportingFirstYearDTO();
        dto.setRegistryReportingFirstYear(2024);

        PermitContainer permitContainer = new PermitContainer();
        permitContainer.setPermitType(PermitType.GHGE);

        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder().status(InstallationAccountStatus.LIVE).build());
        when(reportableEmissionsService.getReportableEmissionsByAccountId(accountId))
                .thenReturn(List.of());

        orchestrator.updateRegistryReportingFirstYear(accountId, dto);

        verify(reportableEmissionsService).getReportableEmissionsByAccountId(accountId);
        verify(installationAccountUpdateService).updateRegistryReportingFirstYear(accountId, dto);
        verify(applicationEventPublisher).publishEvent(any(InstallationAccountUpdatedRegistryEvent.class));
    }

    @Test
    void updateRegistryReportingFirstYear_whenRegistryYearIsBeforeEmissionsYear_shouldUpdateSuccessfully() {

        Long accountId = 1L;
        AccountUpdateRegistryReportingFirstYearDTO dto = new AccountUpdateRegistryReportingFirstYearDTO();
        dto.setRegistryReportingFirstYear(2023);

        PermitContainer permitContainer = new PermitContainer();
        permitContainer.setPermitType(PermitType.GHGE);

        ReportableEmissionsEntity emissionsEntity = new ReportableEmissionsEntity();
        emissionsEntity.setYear(Year.of(2024));

        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder().status(InstallationAccountStatus.LIVE).build());
        when(reportableEmissionsService.getReportableEmissionsByAccountId(accountId))
                .thenReturn(List.of(emissionsEntity));

        orchestrator.updateRegistryReportingFirstYear(accountId, dto);

        verify(reportableEmissionsService).getReportableEmissionsByAccountId(accountId);
        verify(installationAccountUpdateService).updateRegistryReportingFirstYear(accountId, dto);
        verify(applicationEventPublisher).publishEvent(any(InstallationAccountUpdatedRegistryEvent.class));
    }

    @Test
    void updateRegistryReportingFirstYear_whenRegistryYearEqualsEmissionsYear_shouldUpdateSuccessfully() {

        Long accountId = 1L;
        AccountUpdateRegistryReportingFirstYearDTO dto = new AccountUpdateRegistryReportingFirstYearDTO();
        dto.setRegistryReportingFirstYear(2024);

        PermitContainer permitContainer = new PermitContainer();
        permitContainer.setPermitType(PermitType.GHGE);

        ReportableEmissionsEntity emissionsEntity = new ReportableEmissionsEntity();
        emissionsEntity.setYear(Year.of(2024));

        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder().status(InstallationAccountStatus.LIVE).build());
        when(reportableEmissionsService.getReportableEmissionsByAccountId(accountId))
                .thenReturn(List.of(emissionsEntity));

        orchestrator.updateRegistryReportingFirstYear(accountId, dto);

        verify(reportableEmissionsService).getReportableEmissionsByAccountId(accountId);
        verify(installationAccountUpdateService).updateRegistryReportingFirstYear(accountId, dto);
        verify(applicationEventPublisher).publishEvent(any(InstallationAccountUpdatedRegistryEvent.class));
    }

    @Test
    void updateRegistryReportingFirstYear_whenRegistryYearIsAfterEmissionsYear_shouldThrowBusinessException() {

        Long accountId = 1L;
        AccountUpdateRegistryReportingFirstYearDTO dto = new AccountUpdateRegistryReportingFirstYearDTO();
        dto.setRegistryReportingFirstYear(2025);

        PermitContainer permitContainer = new PermitContainer();
        permitContainer.setPermitType(PermitType.GHGE);

        ReportableEmissionsEntity emissionsEntity = new ReportableEmissionsEntity();
        emissionsEntity.setYear(Year.of(2024));

        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder().status(InstallationAccountStatus.LIVE).build());
        when(reportableEmissionsService.getReportableEmissionsByAccountId(accountId))
                .thenReturn(List.of(emissionsEntity));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> orchestrator.updateRegistryReportingFirstYear(accountId, dto));

        assertEquals(MetsErrorCode.REGISTRY_REPORTING_FIRST_YEAR_INVALID_EMISSIONS, exception.getErrorCode());
        verify(reportableEmissionsService).getReportableEmissionsByAccountId(accountId);
        verify(installationAccountUpdateService, never()).updateRegistryReportingFirstYear(accountId, dto);
        verify(applicationEventPublisher, never()).publishEvent(any(InstallationAccountUpdatedRegistryEvent.class));
    }

    @Test
    void updateRegistryReportingFirstYear_whenEmissionsHaveNullYear_shouldUpdateSuccessfully() {

        Long accountId = 1L;
        AccountUpdateRegistryReportingFirstYearDTO dto = new AccountUpdateRegistryReportingFirstYearDTO();
        dto.setRegistryReportingFirstYear(2025);

        PermitContainer permitContainer = new PermitContainer();
        permitContainer.setPermitType(PermitType.GHGE);

        ReportableEmissionsEntity emissionsEntityWithNullYear = new ReportableEmissionsEntity();
        emissionsEntityWithNullYear.setYear(null);

        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder().status(InstallationAccountStatus.LIVE).build());
        when(reportableEmissionsService.getReportableEmissionsByAccountId(accountId))
                .thenReturn(List.of(emissionsEntityWithNullYear));

        orchestrator.updateRegistryReportingFirstYear(accountId, dto);

        verify(reportableEmissionsService).getReportableEmissionsByAccountId(accountId);
        verify(installationAccountUpdateService).updateRegistryReportingFirstYear(accountId, dto);
        verify(applicationEventPublisher).publishEvent(any(InstallationAccountUpdatedRegistryEvent.class));
    }

    @Test
    void updateRegistryReportingFirstYear_whenEmissionsHaveTwoYears_shouldUpdateSuccessfully() {

        Long accountId = 1L;
        AccountUpdateRegistryReportingFirstYearDTO dto = new AccountUpdateRegistryReportingFirstYearDTO();
        dto.setRegistryReportingFirstYear(2024);

        PermitContainer permitContainer = new PermitContainer();
        permitContainer.setPermitType(PermitType.GHGE);

        ReportableEmissionsEntity emissionsEntity = new ReportableEmissionsEntity();
        emissionsEntity.setYear(Year.of(2024));
        ReportableEmissionsEntity emissionsEntity1 = new ReportableEmissionsEntity();
        emissionsEntity1.setYear(Year.of(2025));

        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder().status(InstallationAccountStatus.LIVE).build());
        when(reportableEmissionsService.getReportableEmissionsByAccountId(accountId))
                .thenReturn(List.of(emissionsEntity,emissionsEntity1));

        orchestrator.updateRegistryReportingFirstYear(accountId, dto);

        verify(reportableEmissionsService).getReportableEmissionsByAccountId(accountId);
        verify(installationAccountUpdateService).updateRegistryReportingFirstYear(accountId, dto);
        verify(applicationEventPublisher).publishEvent(any(InstallationAccountUpdatedRegistryEvent.class));
    }

    @Test
    void updateRegistryReportingFirstYear_whenEmissionsHaveTwoYears_shouldThrowBusinessException() {

        Long accountId = 1L;
        AccountUpdateRegistryReportingFirstYearDTO dto = new AccountUpdateRegistryReportingFirstYearDTO();
        dto.setRegistryReportingFirstYear(2025);

        PermitContainer permitContainer = new PermitContainer();
        permitContainer.setPermitType(PermitType.GHGE);

        ReportableEmissionsEntity emissionsEntity = new ReportableEmissionsEntity();
        emissionsEntity.setYear(Year.of(2024));
        ReportableEmissionsEntity emissionsEntity1 = new ReportableEmissionsEntity();
        emissionsEntity1.setYear(Year.of(2025));

        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder().status(InstallationAccountStatus.LIVE).build());
        when(reportableEmissionsService.getReportableEmissionsByAccountId(accountId))
                .thenReturn(List.of(emissionsEntity,emissionsEntity1));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> orchestrator.updateRegistryReportingFirstYear(accountId, dto));

        assertEquals(MetsErrorCode.REGISTRY_REPORTING_FIRST_YEAR_INVALID_EMISSIONS, exception.getErrorCode());
        verify(reportableEmissionsService).getReportableEmissionsByAccountId(accountId);
        verify(installationAccountUpdateService, never()).updateRegistryReportingFirstYear(accountId, dto);
        verify(applicationEventPublisher, never()).publishEvent(any(InstallationAccountUpdatedRegistryEvent.class));
    }
}