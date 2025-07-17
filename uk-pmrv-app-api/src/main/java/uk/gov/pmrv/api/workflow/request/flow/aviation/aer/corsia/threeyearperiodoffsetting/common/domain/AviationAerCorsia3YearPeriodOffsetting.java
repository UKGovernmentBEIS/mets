package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AviationAerCorsia3YearPeriodOffsetting {

    @Builder.Default
    @Size(min = 3, max = 3, message = "Must contain exactly 3 years.")
    private List<Year> schemeYears = new ArrayList<>();

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(min = 3, max = 3, message = "Must contain exactly 3 years.")
    private Map<Year,@Valid @NotNull AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData> yearlyOffsettingData =
            new HashMap<>();

    @Valid
    @NotNull
    private AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData totalYearlyOffsettingData;

    @NotNull(message = "periodOffsettingRequirements is mandatory.")
    @PositiveOrZero(message = "Must be positive number or zero.")
    private Long periodOffsettingRequirements;

    private Boolean operatorHaveOffsettingRequirements;

}
