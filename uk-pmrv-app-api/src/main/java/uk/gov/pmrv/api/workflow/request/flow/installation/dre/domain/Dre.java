package uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.CalculationOfCO2ReportingEmissions;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.DreMonitoringApproachReportingEmissions;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.MeasurementOfCO2ReportingEmissions;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach.MeasurementOfN2OReportingEmissions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{#monitoringApproachReportingEmissions == null || !#monitoringApproachReportingEmissions.containsKey('TRANSFERRED_CO2_N2O')}", 
		message = "dre.monitoringApproaches.transferredco2n2o")
public class Dre {
	
	@Valid
	@NotNull
	private DreDeterminationReason determinationReason;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@Size(max=10000)
	@NotBlank
	private String officialNoticeReason;
	
	@Valid
    @NotEmpty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
	private Map<MonitoringApproachType, @NotNull @Valid DreMonitoringApproachReportingEmissions> monitoringApproachReportingEmissions = new EnumMap<>(
			MonitoringApproachType.class);
	
	@Valid
	@Builder.Default
	@NotEmpty
    private Set<@NotBlank @Size(max = 10000) String> informationSources = new HashSet<>();
	
	@Valid
	@NotNull
	private DreFee fee;
	
	@JsonIgnore
    public BigDecimal getTotalReportableEmissions(){
       return monitoringApproachReportingEmissions.values().stream()
                .map(DreMonitoringApproachReportingEmissions::getApproachTotalEmissions)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(5, RoundingMode.HALF_UP);
    }
	
	@JsonIgnore
	public BigDecimal getTotalTransferredEmissions() {
		return monitoringApproachReportingEmissions.entrySet().stream().map(entry -> {
			final MonitoringApproachType type = entry.getKey();
			final DreMonitoringApproachReportingEmissions approach = entry.getValue();
			switch (type) {
			case CALCULATION_CO2: {
				return ((CalculationOfCO2ReportingEmissions)approach).getTransferredEmissions();
			}
			case MEASUREMENT_CO2: {
				return ((MeasurementOfCO2ReportingEmissions)approach).getTransferredEmissions();
			}
			case MEASUREMENT_N2O: {
				return ((MeasurementOfN2OReportingEmissions)approach).getTransferredEmissions();
			}
			default:
				return BigDecimal.ZERO;
			}})
		.reduce(BigDecimal.ZERO, BigDecimal::add)
		.setScale(5, RoundingMode.HALF_UP);
	}
	
}
