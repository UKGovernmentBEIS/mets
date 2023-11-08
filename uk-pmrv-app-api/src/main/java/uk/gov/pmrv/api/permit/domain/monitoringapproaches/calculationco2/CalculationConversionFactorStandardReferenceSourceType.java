package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CalculationConversionFactorStandardReferenceSourceType {

    MONITORING_REPORTING_REGULATION_ANNEX_II_SECTION_4_2("Monitoring and Reporting Regulation, Annex II, Section 4.2"),
    MONITORING_REPORTING_REGULATION_ANNEX_II_SECTION_4_4("Monitoring and Reporting Regulation, Annex II, Section 4.4"),
    MONITORING_REPORTING_REGULATION_ANNEX_IV_SECTION_9_D("Monitoring and Reporting Regulation, Annex IV, Section 9(D)"),
    OTHER("Other");

    private String description;
}
