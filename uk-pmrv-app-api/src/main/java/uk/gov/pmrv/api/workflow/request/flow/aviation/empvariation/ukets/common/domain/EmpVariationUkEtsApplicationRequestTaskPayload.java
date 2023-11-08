package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain;


import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationApplicationRequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class EmpVariationUkEtsApplicationRequestTaskPayload extends EmpVariationApplicationRequestTaskPayload {

	private EmissionsMonitoringPlanUkEts emissionsMonitoringPlan;

    private ServiceContactDetails serviceContactDetails;
    
    private EmpVariationUkEtsDetails empVariationDetails;
	
	private Boolean empVariationDetailsCompleted;
    
    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return getEmissionsMonitoringPlan() != null ?
            getEmissionsMonitoringPlan().getEmpSectionAttachmentIds() :
            Collections.emptySet();
    }
}
