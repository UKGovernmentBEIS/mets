package uk.gov.pmrv.api.aviationreporting.ukets.domain.verification;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AviationAerInPersonSiteVisitDates {

    @NotNull
    @PastOrPresent
    private LocalDate startDate;

    @NotNull
    @Positive
    private Integer numberOfDays;
}
