package uk.gov.pmrv.api.permit.domain.monitoringapproaches.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MeteringUncertainty {

    LESS_OR_EQUAL_1_5("1.5% or less"),
    LESS_OR_EQUAL_2_5("2.5% or less"),
    LESS_OR_EQUAL_5_0("5% or less"),
    LESS_OR_EQUAL_7_5("7.5% or less"),
    LESS_OR_EQUAL_10_0("10% or less"),
    LESS_OR_EQUAL_12_5("12.5% or less"),
    LESS_OR_EQUAL_15_0("15% or less"),
    LESS_OR_EQUAL_17_5("17.5% or less"),
    N_A("N/A");

    private String description;
}