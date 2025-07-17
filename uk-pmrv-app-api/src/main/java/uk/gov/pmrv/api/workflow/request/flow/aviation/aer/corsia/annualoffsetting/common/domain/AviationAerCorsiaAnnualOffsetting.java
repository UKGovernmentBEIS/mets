package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AviationAerCorsiaAnnualOffsetting {

    @NotNull(message = "Scheme year is mandatory.")
    private Year schemeYear;

    @NotNull(message = "Total chapter 3 state emissions are mandatory.")
    @PositiveOrZero(message = "Must be positive or zero number.")
    private Integer totalChapter;

    @NotNull(message = "Sector growth value is mandatory.")
    @Digits(integer = Integer.MAX_VALUE, fraction = 2, message = "Sector growth value must have up to 2 decimal places.")
    private Double sectorGrowth;


    private Integer calculatedAnnualOffsetting;

}
