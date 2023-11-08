package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.Transfer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.BiomassPercentages;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.DurationRange;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ManuallyProvidedEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReason;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class MeasurementEmissionPointEmission {

    /**
     * Should be populated only for measurementEmissionPointEmission that come from the permit object and are created
     * during the initialization of the aer
     * request payload.
     */
    private String id;

    @NotEmpty
    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<String> sourceStreams = new LinkedHashSet<>();

    @NotEmpty
    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<String> emissionSources = new LinkedHashSet<>();

    @NotBlank
    private String emissionPoint;

    @Valid
    private ParameterMonitoringTierDiffReason parameterMonitoringTierDiffReason;

    @Valid
    @NotNull
    private DurationRange durationRange;

    @Valid
    @NotNull
    private BiomassPercentages biomassPercentages;

    @NotNull
    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal operationalHours;

    @NotNull
    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal annualHourlyAverageFlueGasFlow;

    @NotNull
    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal annualHourlyAverageGHGConcentration;

    @Valid
    private MeasurementAdditionalInformation measurementAdditionalInformation;

    @NotNull
    private Boolean calculationCorrect;

    @Valid
    private ManuallyProvidedEmissions providedEmissions;

    @Valid
    private Transfer transfer;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal reportableEmissions;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal sustainableBiomassEmissions;

    @NotNull
    private BigDecimal globalWarmingPotential;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal annualGasFlow;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal annualFossilAmountOfGreenhouseGas;
}
