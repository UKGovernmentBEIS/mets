package uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.reporting.domain.verification.ReportableAndBiomassEmission;

import java.math.BigDecimal;
import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{#measureTransferredCO2 == (#transferredCO2Emissions != null)}", 
	message = "dre.monitoringApproaches.measurementofco2.transferredco2emissions")
public class MeasurementOfCO2ReportingEmissions extends DreMonitoringApproachReportingEmissions {

	@NotNull
    @Valid
    private ReportableAndBiomassEmission emissions;
	
	private boolean measureTransferredCO2;
	
    @Valid
    private ReportableAndBiomassEmission transferredCO2Emissions;
    
    @Override
	public BigDecimal getApproachTotalEmissions() {
		return List
				.of(emissions.getReportableEmissions(), 
						getTransferredEmissions())
				.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
	}
    
    @JsonIgnore
	public BigDecimal getTransferredEmissions() {
		return measureTransferredCO2 ? transferredCO2Emissions.getReportableEmissions() : BigDecimal.ZERO;
	}
}
