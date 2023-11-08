package uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.monitoringapproach;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.reporting.domain.verification.ReportableEmission;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class InherentCO2ReportingEmissions extends DreMonitoringApproachReportingEmissions {

	@NotNull
    @Valid
    private ReportableEmission totalEmissions;
	
	@Override
	public BigDecimal getApproachTotalEmissions() {
		return totalEmissions.getReportableEmissions();
	}
	
}
