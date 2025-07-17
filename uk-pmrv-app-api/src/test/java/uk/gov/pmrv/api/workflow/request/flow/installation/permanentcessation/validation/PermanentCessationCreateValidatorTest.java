package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.validation;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.validation.InstallationOnsiteInspectionCreateValidator;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermanentCessationCreateValidatorTest {

    @InjectMocks
    private PermanentCessationCreateValidator validator;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Test
    void validateAction() {
        final Long accountId = 1L;

        RequestCreateValidationResult result = RequestCreateValidationResult.builder().valid(true).build();
        Set<AccountStatus> applicableAccountStatuses = validator.getApplicableAccountStatuses();
        Set<RequestType> mutuallyExclusiveRequests = validator.getMutuallyExclusiveRequests();

        when(requestCreateValidatorService.validate(accountId, applicableAccountStatuses, mutuallyExclusiveRequests))
                .thenReturn(result);

        final RequestCreateValidationResult actual = validator.validateAction(accountId);

        assertThat(actual.isValid()).isTrue();
        assertThat(actual.getReportedRequestTypes()).isEmpty();
        assertThat(actual.getReportedAccountStatus()).isNull();

        verify(requestCreateValidatorService, times(1))
                .validate(accountId, applicableAccountStatuses, mutuallyExclusiveRequests);

    }

    @Test
    void getType() {
        assertThat(validator.getType()).isEqualTo(RequestCreateActionType.PERMANENT_CESSATION);
    }

    @Test
    void getApplicableAccountStatuses() {
        assertThat(validator.getApplicableAccountStatuses()).isEqualTo(Set.of(InstallationAccountStatus.LIVE));
    }

    @Test
    void getMutuallyExclusiveRequests() {
        assertThat(validator.getMutuallyExclusiveRequests()).isEqualTo(
                Set.of(RequestType.PERMANENT_CESSATION));
    }
}
