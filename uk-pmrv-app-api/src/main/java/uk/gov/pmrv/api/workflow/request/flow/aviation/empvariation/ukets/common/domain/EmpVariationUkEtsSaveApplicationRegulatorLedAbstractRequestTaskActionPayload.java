package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class EmpVariationUkEtsSaveApplicationRegulatorLedAbstractRequestTaskActionPayload extends RequestTaskActionPayload {

	private EmissionsMonitoringPlanUkEts emissionsMonitoringPlan;
    
    private EmpVariationUkEtsDetails empVariationDetails;
	
	private Boolean empVariationDetailsCompleted;

    @Builder.Default
    private Map<String, List<Boolean>> empSectionsCompleted = new HashMap<>();
}
