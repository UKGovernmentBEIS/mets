package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CalculationEmissionFactorStandardReferenceSourceType {

    UK_NATIONAL_GREENHOUSE_GAS_INVENTORY("UK National Greenhouse Gas Inventory as submitted to the UNFCCC"),
    MONITORING_REPORTING_REGULATION_ARTICLE_36_3("Monitoring and Reporting Regulation, Article 36(3)"),
    MONITORING_REPORTING_REGULATION_ARTICLE_38_2("Monitoring and Reporting Regulation, Article 38(2)"),
    MONITORING_REPORTING_REGULATION_ANNEX_IV_SECTION_1_C1("Monitoring and Reporting Regulation, Annex IV, Section 1(C-1)"),
    MONITORING_REPORTING_REGULATION_ANNEX_IV_SECTION_1_C2("Monitoring and Reporting Regulation, Annex IV, Section 1(C-2)"),
    MONITORING_REPORTING_REGULATION_ANNEX_IV_SECTION_1_D("Monitoring and Reporting Regulation, Annex IV, Section 1(D)"),
    MONITORING_REPORTING_REGULATION_ANNEX_IV_SECTION_2_B("Monitoring and Reporting Regulation, Annex IV, Section 2(B)"),
    MONITORING_REPORTING_REGULATION_ANNEX_IV_SECTION_9_B("Monitoring and Reporting Regulation, Annex IV, Section 9 (B)"),
    MONITORING_REPORTING_REGULATION_ANNEX_IV_SECTION_12_B("Monitoring and Reporting Regulation, Annex IV, Section 12(B)"),
    MONITORING_REPORTING_REGULATION_ANNEX_VI_SECTION_1_TABLE_1("Monitoring and Reporting Regulation, Annex VI, Section 1, Table 1"),
    MONITORING_REPORTING_REGULATION_ANNEX_VI_SECTION_2_TABLE_2("Monitoring and Reporting Regulation, Annex VI, Section 2, Table 2"),
    MONITORING_REPORTING_REGULATION_ANNEX_VI_SECTION_2_TABLE_3("Monitoring and Reporting Regulation, Annex VI, Section 2, Table 3"),
    MONITORING_REPORTING_REGULATION_ANNEX_VI_SECTION_2_TABLE_4("Monitoring and Reporting Regulation, Annex VI, Section 2, Table 4"),
    MONITORING_REPORTING_REGULATION_ANNEX_VI_SECTION_2_TABLE_5("Monitoring and Reporting Regulation, Annex VI, Section 2, Table 5"),
    JEP_GUIDANCE("JEP Guidance for the Monitoring and Reporting of CO2 Emissions from Power Stations"),
    BRITISH_CERAMIC_CONFEDERATION("British Ceramic Confederation (BCC) Methodology (latest version)"),
    LABORATORY_ANALYSIS("Laboratory analysis"),
    PAST_ANALYSIS("Past analysis"),
    SUPPLIER_ANALYSIS("Supplier analysis/data"),
    LITERATURE_VALUE_AGREED_WITH_UK_ETS_REGULATOR("Literature value agreed with UK ETS regulator"),
    IN_HOUSE_CALCULATION("In-house calculation"),
    STOICHIOMETRIC_CALCULATION("Stoichiometric calculation"),
    CONSERVATIVE_ESTIMATION("Conservative estimation"),
    
    OTHER("Other");

    private String description;
    
}
