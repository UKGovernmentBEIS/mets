package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateAccountStatusValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateRequestTypeValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesCreateValidatorTest {

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    private WithholdingOfAllowancesCreateValidator validator;

    @BeforeEach
    void setUp() {
        validator = new WithholdingOfAllowancesCreateValidator(requestCreateValidatorService);
    }

    @Test
    void validateAction_valid() {
        Long accountId = 1L;
        RequestCreateAccountStatusValidationResult accountStatusResult =
                RequestCreateAccountStatusValidationResult.builder().valid(true).build();
        when(requestCreateValidatorService.validateAccountStatuses(eq(accountId), anySet()))
                .thenReturn(accountStatusResult);

        RequestCreateRequestTypeValidationResult conflictingRequestsResult =
                RequestCreateRequestTypeValidationResult.builder().valid(true).build();
        when(requestCreateValidatorService.validateInProgressAndCompletedConflictingRequestTypes(eq(accountId), anySet(),anySet()))
                .thenReturn(conflictingRequestsResult);

        RequestCreateValidationResult result = validator.validateAction(accountId);

        assertTrue(result.isValid());
    }

    @Test
    void getType() {
        RequestCreateActionType type = validator.getType();

        assertEquals(RequestCreateActionType.WITHHOLDING_OF_ALLOWANCES, type);
    }

    @Test
    void getApplicableAccountStatuses() {
        Set<AccountStatus> applicableAccountStatuses = validator.getApplicableAccountStatuses();

        Set<AccountStatus> expectedSet = Set.of(
            InstallationAccountStatus.NEW,
            InstallationAccountStatus.LIVE,
            InstallationAccountStatus.AWAITING_REVOCATION,
            InstallationAccountStatus.AWAITING_SURRENDER,
            InstallationAccountStatus.AWAITING_TRANSFER
        );
        assertEquals(expectedSet, applicableAccountStatuses);
    }

    @Test
    void getMutuallyExclusiveRequests() {
        Set<RequestType> mutuallyExclusiveRequests = validator.getMutuallyExclusiveRequests();
        Set<RequestType> expectedSet = Set.of(RequestType.WITHHOLDING_OF_ALLOWANCES);

        assertEquals(expectedSet, mutuallyExclusiveRequests);
    }
}
