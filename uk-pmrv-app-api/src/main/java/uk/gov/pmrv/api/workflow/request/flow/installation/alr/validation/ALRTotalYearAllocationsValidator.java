package uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRPreliminaryAllocation;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.utils.DoalTotalAllocationsUtils;

import java.time.Year;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ALRTotalYearAllocationsValidator {

    public void validate(Set<ALRPreliminaryAllocation> allocations, Map<Year, Integer> totalAllocationsPerYear) {

        Set<PreliminaryAllocation> preliminaryAllocations = allocations.stream()
                .map(pa -> (PreliminaryAllocation) pa)
                .collect(Collectors.toSet());
        Map<Year, Integer> totalAllocationsPerYearGenerated = DoalTotalAllocationsUtils.generateTotalAllocationsPerYear(preliminaryAllocations);
        MapDifference<Year, Integer> differences = Maps.difference(totalAllocationsPerYearGenerated, totalAllocationsPerYear);

        if(!differences.areEqual()) {
            throw new BusinessException(MetsErrorCode.INVALID_ALR_PRELIMINARY_ALLOCATIONS,
                    differences);
        }
    }
}
