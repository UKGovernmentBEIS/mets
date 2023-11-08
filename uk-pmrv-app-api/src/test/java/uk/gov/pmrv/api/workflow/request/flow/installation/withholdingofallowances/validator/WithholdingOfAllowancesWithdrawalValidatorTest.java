package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingWithdrawal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class WithholdingOfAllowancesWithdrawalValidatorTest {

    private final WithholdingOfAllowancesWithdrawalValidator validator = new WithholdingOfAllowancesWithdrawalValidator();

    @Test
    void validate() {
        assertDoesNotThrow(() -> validator.validate(WithholdingWithdrawal.builder().build()));
    }

}