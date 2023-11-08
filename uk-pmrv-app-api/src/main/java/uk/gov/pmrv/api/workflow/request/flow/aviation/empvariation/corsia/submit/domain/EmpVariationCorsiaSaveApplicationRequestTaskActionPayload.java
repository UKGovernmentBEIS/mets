package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmpVariationCorsiaSaveApplicationRequestTaskActionPayload extends RequestTaskActionPayload {

	private EmissionsMonitoringPlanCorsia emissionsMonitoringPlan;
	
	private EmpVariationCorsiaDetails empVariationDetails;
	
	private Boolean empVariationDetailsCompleted;

    @Builder.Default
    private Map<String, List<Boolean>> empSectionsCompleted = new HashMap<>();
    
    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();
}
