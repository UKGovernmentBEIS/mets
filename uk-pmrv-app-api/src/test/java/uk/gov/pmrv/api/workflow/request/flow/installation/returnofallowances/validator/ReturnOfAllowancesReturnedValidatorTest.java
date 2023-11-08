package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.validator;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturned;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ReturnOfAllowancesReturnedValidatorTest {

    private final ReturnOfAllowancesReturnedValidator validator = new ReturnOfAllowancesReturnedValidator();

    @Test
    void validate() {
        ReturnOfAllowancesReturned returnOfAllowancesReturned = new ReturnOfAllowancesReturned();

        assertDoesNotThrow(() -> validator.validate(returnOfAllowancesReturned));
    }

}