package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmpVariationCorsiaApplicationRequestTaskPayload extends EmpVariationApplicationRequestTaskPayload {

	private EmissionsMonitoringPlanCorsia emissionsMonitoringPlan;

    private ServiceContactDetails serviceContactDetails;
    
    private EmpVariationCorsiaDetails empVariationDetails;
	
	private Boolean empVariationDetailsCompleted;
    
    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return getEmissionsMonitoringPlan() != null ?
            getEmissionsMonitoringPlan().getEmpSectionAttachmentIds() :
            Collections.emptySet();
    }
}
