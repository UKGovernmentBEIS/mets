package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.inherent.InherentCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback.FallbackEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementOfN2OEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationOfPfcEmissions;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = CalculationOfCO2Emissions.class, value = "CALCULATION"),
                @DiscriminatorMapping(schema = MeasurementOfCO2Emissions.class, value = "MEASUREMENT_CO2"),
                @DiscriminatorMapping(schema = FallbackEmissions.class, value = "FALLBACK"),
                @DiscriminatorMapping(schema = MeasurementOfN2OEmissions.class, value = "MEASUREMENT_N2O"),
                @DiscriminatorMapping(schema = InherentCO2Emissions.class, value = "INHERENT_CO2"),
                @DiscriminatorMapping(schema = CalculationOfPfcEmissions.class, value = "CALCULATION_PFC")
        },
        discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CalculationOfCO2Emissions.class, name = "CALCULATION_CO2"),
    @JsonSubTypes.Type(value = MeasurementOfCO2Emissions.class, name = "MEASUREMENT_CO2"),
    @JsonSubTypes.Type(value = CalculationOfPfcEmissions.class, name = "CALCULATION_PFC"),
    @JsonSubTypes.Type(value = MeasurementOfN2OEmissions.class, name = "MEASUREMENT_N2O"),
    @JsonSubTypes.Type(value = FallbackEmissions.class, name = "FALLBACK"),
    @JsonSubTypes.Type(value = InherentCO2Emissions.class, name = "INHERENT_CO2")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class AerMonitoringApproachEmissions {

    private MonitoringApproachType type;

    @JsonIgnore
    public Set<UUID> getAttachmentIds(){
        return Collections.unmodifiableSet(new HashSet<>());
    }
}
