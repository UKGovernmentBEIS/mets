package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsDetails;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmpVariationUkEtsSaveApplicationReviewRequestTaskActionPayload extends RequestTaskActionPayload {

	private EmissionsMonitoringPlanUkEts emissionsMonitoringPlan;
    
    private EmpVariationUkEtsDetails empVariationDetails;
	
	private Boolean empVariationDetailsCompleted;

    @Builder.Default
    private Map<String, List<Boolean>> empSectionsCompleted = new HashMap<>();
    
    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();
    
    private Boolean empVariationDetailsReviewCompleted;
}
