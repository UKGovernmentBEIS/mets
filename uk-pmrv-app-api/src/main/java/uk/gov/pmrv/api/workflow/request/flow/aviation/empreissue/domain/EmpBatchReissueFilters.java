package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.domain;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatusType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueFilters;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#emissionTradingSchemes == null || (!#emissionTradingSchemes.contains('UK_ETS_INSTALLATIONS') && !#emissionTradingSchemes.contains('EU_ETS_INSTALLATIONS'))}", 
	message = "emp.reissue.batch.create.emissionTradingScheme.invalid")
public class EmpBatchReissueFilters extends BatchReissueFilters {

	@NotEmpty
	@Builder.Default
	private Set<EmissionTradingScheme> emissionTradingSchemes = new HashSet<>();
	
	@NotEmpty
	@Builder.Default
	private Set<AviationAccountReportingStatusType> reportingStatuses = new HashSet<>();
	
}
