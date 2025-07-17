package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.MonitoringPlanVersion;

@ExtendWith(MockitoExtension.class)
class AerCreationServiceTest {

    @InjectMocks
    private AerCreationService aerCreationService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private AerCreationValidatorService aerCreationValidatorService;

    @Mock
    private AerDueDateService aerDueDateService;

    @Mock
    private PermitQueryService permitQueryService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private AerInitializationService aerInitializationService;

    @Mock
    private AerMonitoringPlanVersionsBuilderService aerMonitoringPlanVersionsBuilderService;

    @Mock
    private AerPermitOriginatedDataBuilderService aerPermitOriginatedDataBuilderService;

    @Test
    void createRequestAer() {
        Date expirationDate = new Date();
        Long accountId = 1L;

        final InstallationCategory installationCategory = InstallationCategory.A;
        final Aer aer = Aer.builder().build();
        final PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder().build();
        final Permit permit = Permit.builder()
            .build();
        final PermitContainer permitContainer = PermitContainer.builder()
            .permitType(PermitType.GHGE)
            .permit(permit)
            .build();
        final String permitId = "permitId";
        final Year reportingYear = Year.now().minusYears(1);
        final List<MonitoringPlanVersion> monitoringPlanVersions = new ArrayList<>();

        RequestType requestTypeInitiatedBy = RequestType.PERMIT_REVOCATION;

        when(aerCreationValidatorService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(aerCreationValidatorService.validateYear(eq(accountId), any()))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(aerDueDateService.generateDueDate())
                .thenReturn(expirationDate);
        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
        when(installationOperatorDetailsQueryService.getInstallationCategory(accountId)).thenReturn(installationCategory);
        when(aerInitializationService.initializeAer(permit)).thenReturn(aer);
        when(aerPermitOriginatedDataBuilderService.buildPermitOriginatedData(accountId, aer, permitContainer, installationCategory))
            .thenReturn(permitOriginatedData);
        when(permitQueryService.getPermitIdByAccountId(accountId)).thenReturn(Optional.of(permitId));
        when(aerMonitoringPlanVersionsBuilderService.buildMonitoringPlanVersions(accountId, permitId, reportingYear))
            .thenReturn(monitoringPlanVersions);

        // Invoke
        aerCreationService.createRequestAer(accountId, requestTypeInitiatedBy);

        // Verify
        verify(aerCreationValidatorService, times(1)).validateAccountStatus(accountId);
        verify(aerCreationValidatorService, times(1)).validateYear(eq(accountId), any());
        verify(aerDueDateService, times(1)).generateDueDate();
        verify(permitQueryService, times(1)).getPermitContainerByAccountId(accountId);
        verify(installationOperatorDetailsQueryService, times(1)).getInstallationCategory(accountId);
        verify(aerInitializationService, times(1)).initializeAer(permit);
        verify(aerPermitOriginatedDataBuilderService, times(1)).buildPermitOriginatedData(accountId, aer, permitContainer, installationCategory);
        verify(permitQueryService, times(1)).getPermitIdByAccountId(accountId);
        verify(aerMonitoringPlanVersionsBuilderService, times(1)).buildMonitoringPlanVersions(accountId, permitId, reportingYear);
        verify(startProcessRequestService, times(1)).startProcess(any());
    }

    @Test
    void createRequestAer_throw_account_exception() {
        Long accountId = 1L;
        RequestType requestTypeInitiatedBy = RequestType.PERMIT_REVOCATION;

        when(aerCreationValidatorService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                aerCreationService.createRequestAer(accountId, requestTypeInitiatedBy));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(MetsErrorCode.AER_CREATION_NOT_ALLOWED);
        verify(startProcessRequestService, never()).startProcess(any());
    }

    @Test
    void createRequestAer_throw_same_year_exception() {
        Long accountId = 1L;
        RequestType requestTypeInitiatedBy = RequestType.PERMIT_REVOCATION;

        when(aerCreationValidatorService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(aerCreationValidatorService.validateYear(eq(accountId), any()))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                aerCreationService.createRequestAer(accountId, requestTypeInitiatedBy));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(MetsErrorCode.AER_CREATION_NOT_ALLOWED);
        verify(startProcessRequestService, never()).startProcess(any());
    }
}
