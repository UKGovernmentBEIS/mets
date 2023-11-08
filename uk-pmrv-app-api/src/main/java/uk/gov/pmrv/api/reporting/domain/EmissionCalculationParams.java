package uk.gov.pmrv.api.reporting.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EmissionCalculationParams {

    @Column(name = "emission_factor")
    @NotNull
    private BigDecimal emissionFactor;

    @Column(name = "net_calorific_value")
    @NotNull
    private BigDecimal netCalorificValue;

    @Column(name = "oxidation_factor")
    @NotNull
    private BigDecimal oxidationFactor;

    @Enumerated(EnumType.STRING)
    @Column(name = "ncv_measurement_unit")
    @NotNull
    private NCVMeasurementUnit ncvMeasurementUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "ef_measurement_unit")
    @NotNull
    private EmissionFactorMeasurementUnit efMeasurementUnit;
}
