package uk.gov.pmrv.api.workflow.request.flow.installation.air.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CalculationParameter {
    
    AD("Activity data"),
    NCV("Net calorific value"),
    OF("Oxidation factor"),
    CF("Conversion factor"),
    BF("Biomass fraction"),
    EF("Emission factor"),
    CC("Carbon content");

    private final String description;
}
