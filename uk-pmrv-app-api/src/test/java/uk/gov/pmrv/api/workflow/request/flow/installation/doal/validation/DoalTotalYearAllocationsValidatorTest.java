package uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

import java.time.Year;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DoalTotalYearAllocationsValidatorTest {

    @InjectMocks
    private DoalTotalYearAllocationsValidator validator;

    @Test
    void validate() {
        Set<PreliminaryAllocation> allocations = Set.of(
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.ALUMINIUM)
                        .year(Year.of(2022))
                        .allowances(10)
                        .build(),
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AROMATICS)
                        .year(Year.of(2022))
                        .allowances(20)
                        .build(),
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AMMONIA)
                        .year(Year.of(2023))
                        .allowances(400)
                        .build()
        );
        Map<Year, Integer> totalAllocationsPerYear = Map.of(
                Year.of(2022), 30,
                Year.of(2023), 400
        );

        // Invoke
        validator.validate(allocations, totalAllocationsPerYear);
    }

    @Test
    void validate_empty() {
        // Invoke
        validator.validate(Set.of(), Map.of());
    }

    @Test
    void validate_not_valid() {
        Set<PreliminaryAllocation> allocations = Set.of(
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AROMATICS)
                        .year(Year.of(2022))
                        .allowances(20)
                        .build(),
                PreliminaryAllocation.builder()
                        .subInstallationName(SubInstallationName.AMMONIA)
                        .year(Year.of(2023))
                        .allowances(400)
                        .build()
        );
        Map<Year, Integer> totalAllocationsPerYear = Map.of(
                Year.of(2022), 30,
                Year.of(2023), 400
        );

        // Invoke
        final BusinessException businessException = assertThrows(BusinessException.class,
                () -> validator.validate(allocations, totalAllocationsPerYear));

        // Verify
        assertEquals(MetsErrorCode.INVALID_DOAL, businessException.getErrorCode());
    }
}
