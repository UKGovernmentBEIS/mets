package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderCreateValidatorTest {

    @InjectMocks
    private PermitSurrenderCreateValidator validator;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;
    
    @Test
    void validateAction() {
        Long accountId = 1L;

        RequestCreateValidationResult result = RequestCreateValidationResult.builder().valid(true).build();
        Set<AccountStatus> applicableAccountStatuses = Set.of(InstallationAccountStatus.LIVE);
        Set<RequestType> mutuallyExclusiveRequests = Set.of(
                RequestType.PERMIT_SURRENDER,
                RequestType.PERMIT_VARIATION,
                RequestType.PERMIT_TRANSFER_A);

        when(requestCreateValidatorService.validate(accountId, applicableAccountStatuses, mutuallyExclusiveRequests))
                .thenReturn(result);

        // Invoke
        final RequestCreateValidationResult actual = validator.validateAction(accountId);

        // Verify
        assertThat(actual.isValid()).isTrue();
        assertThat(actual.getReportedRequestTypes()).isEmpty();
        assertThat(actual.getReportedAccountStatus()).isNull();

        verify(requestCreateValidatorService, times(1))
                .validate(accountId, applicableAccountStatuses, mutuallyExclusiveRequests);
    }

    @Test
    void getApplicableAccountStatuses() {
        Set<AccountStatus> accountStatuses = Set.of(InstallationAccountStatus.LIVE);

        assertThat(validator.getApplicableAccountStatuses()).isEqualTo(accountStatuses);
    }

    @Test
    void getMutuallyExclusiveRequests() {
        Set<RequestType> mutuallyExclusiveRequests = Set.of(
                RequestType.PERMIT_SURRENDER,
                RequestType.PERMIT_VARIATION,
                RequestType.PERMIT_TRANSFER_A);

        assertThat(validator.getMutuallyExclusiveRequests()).isEqualTo(mutuallyExclusiveRequests);
    }

    @Test
    void type() {
        RequestCreateActionType actionType = validator.getType();

        assertThat(actionType).isEqualTo(RequestCreateActionType.PERMIT_SURRENDER);
    }
}
