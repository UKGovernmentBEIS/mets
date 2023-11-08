package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationCreateValidatorTest {

    @InjectMocks
    private PermitNotificationCreateValidator validator;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Test
    void validateAction() {
        Long accountId = 1L;
        RequestCreateValidationResult result = RequestCreateValidationResult.builder().valid(true).build();
        Set<AccountStatus> applicableAccountStatuses = Set.of(
                InstallationAccountStatus.LIVE,
                InstallationAccountStatus.AWAITING_SURRENDER,
                InstallationAccountStatus.SURRENDERED,
                InstallationAccountStatus.AWAITING_REVOCATION,
                InstallationAccountStatus.REVOKED,
                InstallationAccountStatus.AWAITING_TRANSFER,
                InstallationAccountStatus.TRANSFERRED);

        when(requestCreateValidatorService.validate(accountId, applicableAccountStatuses, Set.of()))
                .thenReturn(result);

        // Invoke
        RequestCreateValidationResult actual = validator.validateAction(accountId);

        // Verify
        assertThat(actual.isValid()).isTrue();
        assertThat(actual.getReportedRequestTypes()).isEmpty();
        assertThat(actual.getReportedAccountStatus()).isNull();

        verify(requestCreateValidatorService, times(1))
                .validate(accountId, applicableAccountStatuses, Set.of());
    }

    @Test
    void getApplicableAccountStatuses() {
        Set<AccountStatus> accountStatuses = Set.of(
                InstallationAccountStatus.LIVE,
                InstallationAccountStatus.AWAITING_SURRENDER,
                InstallationAccountStatus.SURRENDERED,
                InstallationAccountStatus.AWAITING_REVOCATION,
                InstallationAccountStatus.REVOKED,
                InstallationAccountStatus.AWAITING_TRANSFER,
                InstallationAccountStatus.TRANSFERRED);

        assertThat(validator.getApplicableAccountStatuses()).isEqualTo(accountStatuses);
    }

    @Test
    void getMutuallyExclusiveRequests() {
        assertThat(validator.getMutuallyExclusiveRequests()).isEqualTo(Set.of());
    }

    @Test
    void type() {
        RequestCreateActionType actionType = validator.getType();
        assertThat(actionType).isEqualTo(RequestCreateActionType.PERMIT_NOTIFICATION);
    }
}
