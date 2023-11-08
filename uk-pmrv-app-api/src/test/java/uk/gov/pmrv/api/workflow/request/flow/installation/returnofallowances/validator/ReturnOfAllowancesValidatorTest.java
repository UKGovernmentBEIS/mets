package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.validator;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowances;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ReturnOfAllowancesValidatorTest {

    private final ReturnOfAllowancesValidator validator = new ReturnOfAllowancesValidator();

    @Test
    void validate() {
        ReturnOfAllowances returnOfAllowances = new ReturnOfAllowances();

        assertDoesNotThrow(() -> validator.validate(returnOfAllowances));
    }

}