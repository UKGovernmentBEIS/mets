package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowances;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class WithholdingOfAllowancesValidatorTest {

    private final WithholdingOfAllowancesValidator validator = new WithholdingOfAllowancesValidator();

    @Test
    void validate() {
        WithholdingOfAllowances withholdingOfAllowances = new WithholdingOfAllowances();

        assertDoesNotThrow(() -> validator.validate(withholdingOfAllowances));
    }

}