package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator.WithholdingOfAllowancesCreateValidator;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WithholdingOfAllowancesCreateValidatorTest {

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    private WithholdingOfAllowancesCreateValidator validator;

    @BeforeEach
    void setUp() {
        validator = new WithholdingOfAllowancesCreateValidator(requestCreateValidatorService);
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

        assertEquals(Collections.emptySet(), mutuallyExclusiveRequests);
    }
}
