package uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.reporting.domain.verification.ReportableAndBiomassEmission;

import java.math.BigDecimal;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class FallbackReportingEmissions extends DreMonitoringApproachReportingEmissions {

	@NotNull
    @Valid
    private ReportableAndBiomassEmission emissions;
	
	@Override
	public BigDecimal getApproachTotalEmissions() {
		return getEmissions().getReportableEmissions();
	}
	
}
