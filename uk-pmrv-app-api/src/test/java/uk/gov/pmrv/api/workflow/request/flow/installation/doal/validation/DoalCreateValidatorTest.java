package uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalCreateValidatorTest {

    @InjectMocks
    private DoalCreateValidator validator;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Test
    void validateAction() {
        final Long accountId = 1L;
        final Set<AccountStatus> applicableAccountStatuses = Set.of(
                InstallationAccountStatus.LIVE,
                InstallationAccountStatus.AWAITING_REVOCATION,
                InstallationAccountStatus.AWAITING_SURRENDER
        );
        final Set<RequestType> mutuallyExclusiveRequests = Set.of(RequestType.DOAL);

        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder().emitterType(EmitterType.GHGE).build());
        when(requestCreateValidatorService.validate(accountId, applicableAccountStatuses, mutuallyExclusiveRequests))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(accountId);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());
        verify(installationAccountQueryService, times(1))
                .getAccountDTOById(accountId);
        verify(requestCreateValidatorService, times(1))
                .validate(accountId, applicableAccountStatuses, mutuallyExclusiveRequests);
    }

    @Test
    void validateAction_no_valid() {
        final Long accountId = 1L;

        when(installationAccountQueryService.getAccountDTOById(accountId))
                .thenReturn(InstallationAccountDTO.builder().emitterType(EmitterType.HSE).build());

        // Invoke
        RequestCreateValidationResult result = validator.validateAction(accountId);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().isAvailable(false).build());
        verify(installationAccountQueryService, times(1))
                .getAccountDTOById(accountId);
        verifyNoInteractions(requestCreateValidatorService);
    }

    @Test
    void getType() {
        assertThat(validator.getType()).isEqualTo(RequestCreateActionType.DOAL);
    }
}
