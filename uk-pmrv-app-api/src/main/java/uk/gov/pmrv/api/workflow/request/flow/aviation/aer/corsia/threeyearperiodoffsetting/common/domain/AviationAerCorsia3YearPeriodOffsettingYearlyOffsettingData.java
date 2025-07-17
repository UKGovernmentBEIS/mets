package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData {

    @NotNull(message = "calculatedAnnualOffsetting is mandatory.")
    @PositiveOrZero(message = "Must be positive number.")
    private Long calculatedAnnualOffsetting;

    @NotNull(message = "cefEmissionsReductions is mandatory.")
    @PositiveOrZero(message = "Must be positive number.")
    private Long cefEmissionsReductions;
}
