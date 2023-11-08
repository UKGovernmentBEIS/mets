package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CalculationNetCalorificValueStandardReferenceSourceType {

    UK_NATIONAL_GREENHOUSE_GAS_INVENTORY("UK National Greenhouse Gas Inventory as submitted to the UNFCCC"),
    MONITORING_AND_REPORTING_REGULATION("Monitoring and Reporting Regulation, Annex VI, Section 1, Table 1"),
    JEP_GUIDANCE_FOR_MONITORING_AND_REPORTING("JEP Guidance for Monitoring and Reporting of CO2 from Power Stations"),
    LABORATORY_ANALYSIS("Laboratory analysis"),
    SITE_SPECIFIC_ANALYSIS("Site specific analysis"),
    PAST_ANALYSIS("Past analysis"),
    SUPPLIER_ANALYSIS_DATA("Supplier analysis/data"),
    LITERATURE_VALUE_AGREED("Literature value agreed with UK ETS regulator"),
    IN_HOUSE_CALCULATION("In-house calculation"),
    STOICHIOMETRIC_CALCULATION("Stoichiometric calculation"),
    CONSERVATIVE_ESTIMATION("Conservative estimation"),
    OTHER("Other");

    private String description;
}
