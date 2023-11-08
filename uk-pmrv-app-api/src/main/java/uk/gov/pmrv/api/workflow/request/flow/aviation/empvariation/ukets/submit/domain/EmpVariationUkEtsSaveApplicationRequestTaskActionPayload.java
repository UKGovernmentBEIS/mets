package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsSaveApplicationRegulatorLedAbstractRequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmpVariationUkEtsSaveApplicationRequestTaskActionPayload
		extends EmpVariationUkEtsSaveApplicationRegulatorLedAbstractRequestTaskActionPayload {

	@Builder.Default
	private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();

}
