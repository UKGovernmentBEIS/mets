package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

@ExtendWith(MockitoExtension.class)
class AviationAccountClosureCreateValidatorTest {

	@InjectMocks
    private AviationAccountClosureCreateValidator validator;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Test
    void validateAction_whenValid_thenOk() {

        final Long accountId = 1L;

        RequestCreateValidationResult result = RequestCreateValidationResult.builder().valid(true).build();
        Set<AccountStatus> applicableAccountStatuses = Set.of(AviationAccountStatus.NEW, AviationAccountStatus.LIVE);
        Set<RequestType> mutuallyExclusiveRequests = Set.of(RequestType.AVIATION_ACCOUNT_CLOSURE);

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
        Set<AccountStatus> accountStatuses = Set.of(AviationAccountStatus.NEW, AviationAccountStatus.LIVE);

        assertThat(validator.getApplicableAccountStatuses()).isEqualTo(accountStatuses);
    }

    @Test
    void getMutuallyExclusiveRequests() {
        Set<RequestType> mutuallyExclusiveRequests = Set.of(RequestType.AVIATION_ACCOUNT_CLOSURE);

        assertThat(validator.getMutuallyExclusiveRequests()).isEqualTo(mutuallyExclusiveRequests);
    }

    @Test
    void type() {
        assertThat(validator.getType()).isEqualTo(RequestCreateActionType.AVIATION_ACCOUNT_CLOSURE);
    }
}
