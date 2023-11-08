package uk.gov.pmrv.api.workflow.request.flow.installation.doal.utils;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class DoalTotalAllocationsUtils {

    public Map<Year, Integer> generateTotalAllocationsPerYear(final Set<PreliminaryAllocation> allocations) {
        Map<Year, List<PreliminaryAllocation>> allocationsPerYear = allocations.stream()
                .collect(Collectors.groupingBy(PreliminaryAllocation::getYear));

        return allocationsPerYear.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToInt(PreliminaryAllocation::getAllowances)
                                .sum()
                ));
    }
}
