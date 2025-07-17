package uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.allowance.domain.PreliminaryAllocation;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalViolation;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.utils.DoalTotalAllocationsUtils;

import java.time.Year;
import java.util.Map;
import java.util.Set;

@Service
public class DoalTotalYearAllocationsValidator {

    public void validate(Set<PreliminaryAllocation> allocations, Map<Year, Integer> totalAllocationsPerYear) {
        Map<Year, Integer> totalAllocationsPerYearGenerated = DoalTotalAllocationsUtils.generateTotalAllocationsPerYear(allocations);
        MapDifference<Year, Integer> differences = Maps.difference(totalAllocationsPerYearGenerated, totalAllocationsPerYear);

        if(!differences.areEqual()) {
            throw new BusinessException(MetsErrorCode.INVALID_DOAL,
                    DoalViolation.INVALID_TOTAL_ALLOCATIONS_PER_YEAR.getMessage(),
                    differences.entriesDiffering());
        }
    }
}
