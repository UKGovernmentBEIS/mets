package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CalculationOxidationFactorStandardReferenceSourceType {

    UK_NATIONAL_GREENHOUSE_GAS_INVENTORY("UK National Greenhouse Gas Inventory as submitted to the UNFCCC"),
    MONITORING_REPORTING_REGULATION_ANNEX_II_SECTION_2_3("Monitoring and Reporting Regulation, Annex II, Section 2.3"),
    LABORATORY_ANALYSIS("Laboratory analysis"),
    
    OTHER("Other");

    private String description;
}
