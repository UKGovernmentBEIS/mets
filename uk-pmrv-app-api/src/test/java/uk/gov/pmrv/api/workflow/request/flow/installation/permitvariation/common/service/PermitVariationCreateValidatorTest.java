package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service;

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

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

@ExtendWith(MockitoExtension.class)
class PermitVariationCreateValidatorTest {

	@InjectMocks
    private PermitVariationCreateValidator validator;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Test
    void validateAction() {
        final Long accountId = 1L;

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
    void getType() {
        assertThat(validator.getType()).isEqualTo(RequestCreateActionType.PERMIT_VARIATION);
    }
    
    @Test
    void getApplicableAccountStatuses() {
        assertThat(validator.getApplicableAccountStatuses()).isEqualTo(Set.of(InstallationAccountStatus.LIVE));
    }
    
    @Test
    void getMutuallyExclusiveRequests() {
		assertThat(validator.getMutuallyExclusiveRequests()).isEqualTo(
				Set.of(RequestType.PERMIT_VARIATION, RequestType.PERMIT_TRANSFER_A, RequestType.PERMIT_SURRENDER));
    }
}
