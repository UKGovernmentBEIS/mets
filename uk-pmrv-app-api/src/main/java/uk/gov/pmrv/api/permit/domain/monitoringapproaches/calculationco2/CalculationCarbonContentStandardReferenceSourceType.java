package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CalculationCarbonContentStandardReferenceSourceType {

    MONITORING_REPORTING_REGULATION_ARTICLE_25_1("Monitoring and Reporting Regulation, Article 25(1)"),
    MONITORING_REPORTING_REGULATION_ANNEX_VI_SECTION2_TABLE_4("Monitoring and Reporting Regulation, Annex VI, Section 2, Table 4"),
    UK_STEEL_CARBON_IN_SCRAP_AND_ALLOYS_PROTOCOL_LATEST_VERSION("UK Steel Carbon in Scrap and Alloys Protocol (latest version)"),
    LABORATORY_ANALYSIS("Laboratory analysis"),
    SUPPLIER_ANALYSIS_DATA("Supplier analysis/data"),
    PAST_ANALYSIS("Past analysis"),
    IN_HOUSE_CALCULATION("In-house calculation"),
    OTHER("Other");

    private String description;
}
