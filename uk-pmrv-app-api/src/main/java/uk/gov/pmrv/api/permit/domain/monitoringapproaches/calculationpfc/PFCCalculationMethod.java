package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PFCCalculationMethod {
    
    SLOPE("Slope"),
    OVERVOLTAGE("Overvoltage");

    private final String description;
}
