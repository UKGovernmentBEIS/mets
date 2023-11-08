package uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach;

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

import java.math.BigDecimal;

@Schema(
		discriminatorMapping = {
				@DiscriminatorMapping(schema = CalculationOfCO2ReportingEmissions.class, value = "CALCULATION_CO2"),
				@DiscriminatorMapping(schema = MeasurementOfCO2ReportingEmissions.class, value = "MEASUREMENT_CO2"),
				@DiscriminatorMapping(schema = MeasurementOfN2OReportingEmissions.class, value = "MEASUREMENT_N2O"),
				@DiscriminatorMapping(schema = CalculationOfPFCReportingEmissions.class, value = "CALCULATION_PFC"),
				@DiscriminatorMapping(schema = InherentCO2ReportingEmissions.class, value = "INHERENT_CO2"),
				@DiscriminatorMapping(schema = FallbackReportingEmissions.class, value = "FALLBACK"),
		},
		discriminatorProperty = "type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CalculationOfCO2ReportingEmissions.class, name = "CALCULATION_CO2"),
    @JsonSubTypes.Type(value = MeasurementOfCO2ReportingEmissions.class, name = "MEASUREMENT_CO2"),
    @JsonSubTypes.Type(value = MeasurementOfN2OReportingEmissions.class, name = "MEASUREMENT_N2O"),
    @JsonSubTypes.Type(value = CalculationOfPFCReportingEmissions.class, name = "CALCULATION_PFC"),
    @JsonSubTypes.Type(value = InherentCO2ReportingEmissions.class, name = "INHERENT_CO2"),
    @JsonSubTypes.Type(value = FallbackReportingEmissions.class, name = "FALLBACK"),
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class DreMonitoringApproachReportingEmissions {

	private MonitoringApproachType type;
	
	@JsonIgnore
	public abstract BigDecimal getApproachTotalEmissions();
	
}
