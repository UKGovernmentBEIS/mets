package uk.gov.pmrv.api.allowance.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;

import java.time.Year;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AllowanceAllocationValidatorTest {

    @InjectMocks
    private AllowanceAllocationValidator validator;

    @Test
    void isValid() {
        final Set<PreliminaryAllocation> allocations = Set.of(
                PreliminaryAllocation.builder().year(Year.of(2021)).subInstallationName(SubInstallationName.ALUMINIUM).allowances(10).build(),
                PreliminaryAllocation.builder().year(Year.of(2022)).subInstallationName(SubInstallationName.ALUMINIUM).allowances(10).build()
        );

        boolean result = validator.isValid(allocations);

        assertTrue(result);
    }

    @Test
    void isValid_not_valid() {
        final Set<PreliminaryAllocation> allocations = Set.of(
                PreliminaryAllocation.builder().year(Year.of(2022)).subInstallationName(SubInstallationName.ALUMINIUM).allowances(10).build(),
                PreliminaryAllocation.builder().year(Year.of(2022)).subInstallationName(SubInstallationName.ALUMINIUM).allowances(20).build()
        );

        boolean result = validator.isValid(allocations);

        assertFalse(result);
    }
}
