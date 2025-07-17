package uk.gov.pmrv.api.allowance.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Validated
public class AllowanceAllocationValidator {

    public boolean isValid(@NotEmpty Set<@NotNull @Valid PreliminaryAllocation> allocations) {
        return allocations.stream()
                .collect(Collectors.groupingBy(pa -> Arrays.asList(pa.getYear(), pa.getSubInstallationName())))
                .entrySet().stream()
                .noneMatch(entry -> entry.getValue().size() > 1);
    }
}
