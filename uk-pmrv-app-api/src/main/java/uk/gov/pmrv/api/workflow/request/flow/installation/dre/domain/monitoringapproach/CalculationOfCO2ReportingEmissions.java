package uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.reporting.domain.verification.ReportableAndBiomassEmission;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{#calculateTransferredCO2 == (#transferredCO2Emissions != null)}", 
	message = "dre.monitoringApproaches.calculationofco2.transferredco2emissions")
public class CalculationOfCO2ReportingEmissions extends DreMonitoringApproachReportingEmissions {

	@NotNull
    @Valid
    private ReportableAndBiomassEmission combustionEmissions;
	
	@NotNull
    @Valid
    private ReportableAndBiomassEmission processEmissions;
	
	@NotNull
    @Valid
    private ReportableAndBiomassEmission massBalanceEmissions;
	
	private boolean calculateTransferredCO2;
	
    @Valid
    private ReportableAndBiomassEmission transferredCO2Emissions;

	@Override
	public BigDecimal getApproachTotalEmissions() {
		return List
				.of(combustionEmissions.getReportableEmissions(), 
						processEmissions.getReportableEmissions(),
						massBalanceEmissions.getReportableEmissions(),
						getTransferredEmissions())
				.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	@JsonIgnore
	public BigDecimal getTransferredEmissions() {
		return calculateTransferredCO2 ? transferredCO2Emissions.getReportableEmissions() : BigDecimal.ZERO;
	}
}
