package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationParameterType;

import jakarta.validation.constraints.NotNull;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = CalculationActivityDataMonitoringTier.class, value = "ACTIVITY_DATA"),
                @DiscriminatorMapping(schema = CalculationNetCalorificValueMonitoringTier.class, value = "NET_CALORIFIC_VALUE"),
                @DiscriminatorMapping(schema = CalculationEmissionFactorMonitoringTier.class, value = "EMISSION_FACTOR"),
                @DiscriminatorMapping(schema = CalculationOxidationFactorMonitoringTier.class, value = "OXIDATION_FACTOR"),
                @DiscriminatorMapping(schema = CalculationCarbonContentMonitoringTier.class, value = "CARBON_CONTENT"),
                @DiscriminatorMapping(schema = CalculationConversionFactorMonitoringTier.class, value = "CONVERSION_FACTOR"),
                @DiscriminatorMapping(schema = CalculationBiomassFractionMonitoringTier.class, value = "BIOMASS_FRACTION")

        },
        discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CalculationActivityDataMonitoringTier.class, name = "ACTIVITY_DATA"),
    @JsonSubTypes.Type(value = CalculationNetCalorificValueMonitoringTier.class, name = "NET_CALORIFIC_VALUE"),
    @JsonSubTypes.Type(value = CalculationEmissionFactorMonitoringTier.class, name = "EMISSION_FACTOR"),
    @JsonSubTypes.Type(value = CalculationOxidationFactorMonitoringTier.class, name = "OXIDATION_FACTOR"),
    @JsonSubTypes.Type(value = CalculationCarbonContentMonitoringTier.class, name = "CARBON_CONTENT"),
    @JsonSubTypes.Type(value = CalculationConversionFactorMonitoringTier.class, name = "CONVERSION_FACTOR"),
    @JsonSubTypes.Type(value = CalculationBiomassFractionMonitoringTier.class, name = "BIOMASS_FRACTION"),
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class CalculationParameterMonitoringTier {

    @NotNull
    private CalculationParameterType type;
}
