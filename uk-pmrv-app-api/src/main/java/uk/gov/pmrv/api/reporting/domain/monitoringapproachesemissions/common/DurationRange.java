package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#fullYearCovered) == (#coverageStartDate == null && #coverageEndDate == null)}",
    message = "aer.sourceStreamEmissions.durationRange.fullYearCovered")
@SpELExpression(expression = "{(#coverageStartDate == null) || (#coverageEndDate == null) || " +
    "T(java.time.LocalDate).parse(#coverageStartDate).isBefore(T(java.time.LocalDate).parse(#coverageEndDate))}",
    message = "aer.sourceStreamEmissions.durationRange.coverageStartDate.coverageEndDate")
public class DurationRange {
    private Boolean fullYearCovered;

    private LocalDate coverageStartDate;

    private LocalDate coverageEndDate;
}
