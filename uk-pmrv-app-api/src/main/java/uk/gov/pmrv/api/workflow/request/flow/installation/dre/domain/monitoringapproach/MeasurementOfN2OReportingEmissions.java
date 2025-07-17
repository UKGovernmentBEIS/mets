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
@SpELExpression(expression = "{#measureTransferredN2O == (#transferredN2OEmissions != null)}", 
	message = "dre.monitoringApproaches.measurementofn2o.transferredn2oemissions")
public class MeasurementOfN2OReportingEmissions extends DreMonitoringApproachReportingEmissions {

	@NotNull
    @Valid
    private ReportableAndBiomassEmission emissions;
	
	private boolean measureTransferredN2O;
	
    @Valid
    private ReportableAndBiomassEmission transferredN2OEmissions;
    
    @Override
	public BigDecimal getApproachTotalEmissions() {
		return List
				.of(emissions.getReportableEmissions(), 
						getTransferredEmissions())
				.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
	}
    
    @JsonIgnore
	public BigDecimal getTransferredEmissions() {
		return measureTransferredN2O ? transferredN2OEmissions.getReportableEmissions() : BigDecimal.ZERO;
	}
}
