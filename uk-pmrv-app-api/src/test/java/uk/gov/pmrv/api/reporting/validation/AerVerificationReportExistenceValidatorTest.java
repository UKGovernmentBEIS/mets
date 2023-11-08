package uk.gov.pmrv.api.reporting.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerVerificationReportExistenceValidatorTest {

    @InjectMocks
    private AerVerificationReportExistenceValidator aerVerificationReportExistenceValidator;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Test
    void validate() {
        final AerContainer aerContainer = AerContainer.builder()
                .verificationReport(AerVerificationReport.builder().verificationBodyId(1L).build())
                .build();
        final long accountId = 1L;
        final InstallationAccountInfoDTO accountInfo = InstallationAccountInfoDTO.builder().emitterType(EmitterType.GHGE).build();

        when(installationAccountQueryService.getInstallationAccountInfoDTOById(accountId)).thenReturn(accountInfo);

        // Invoke
        aerVerificationReportExistenceValidator.validate(aerContainer, accountId);

        // Verify
        verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(accountId);
    }

    @Test
    void validate_not_valid() {
        final long accountId = 1L;
        final AerContainer aerContainer = AerContainer.builder().build();
        final InstallationAccountInfoDTO accountInfo = InstallationAccountInfoDTO.builder().emitterType(EmitterType.GHGE).build();

        when(installationAccountQueryService.getInstallationAccountInfoDTOById(accountId)).thenReturn(accountInfo);

        // Invoke
        BusinessException be = assertThrows(BusinessException.class,
                () -> aerVerificationReportExistenceValidator.validate(aerContainer, accountId));

        // Verify
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_AER);
        verify(installationAccountQueryService, times(1)).getInstallationAccountInfoDTOById(accountId);
    }
}
