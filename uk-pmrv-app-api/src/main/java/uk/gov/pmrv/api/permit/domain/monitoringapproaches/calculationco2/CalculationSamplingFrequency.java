package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CalculationSamplingFrequency {

    CONTINUOUS("Continuous"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    QUARTERLY("Quarterly"),
    BI_ANNUALLY("Biannually"),
    ANNUALLY("Annually"),
    OTHER("Other");

    private String description;
}
