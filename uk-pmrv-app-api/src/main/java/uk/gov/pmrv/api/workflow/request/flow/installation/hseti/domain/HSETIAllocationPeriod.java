package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain;


import lombok.Getter;
import java.time.Year;

@Getter
public enum HSETIAllocationPeriod {

    PERIOD_2021_2025(Year.of(2021), Year.of(2025)),
    PERIOD_2026_2030(Year.of(2026), Year.of(2030));

    private final Year periodFrom;
    private final Year periodTo;

    HSETIAllocationPeriod(Year periodFrom, Year periodTo) {
        this.periodFrom = periodFrom;
        this.periodTo = periodTo;
    }

}
