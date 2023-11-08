package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.common.domain;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceApplicationRequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class EmpIssuanceCorsiaApplicationRequestTaskPayload extends EmpIssuanceApplicationRequestTaskPayload {

    private EmissionsMonitoringPlanCorsia emissionsMonitoringPlan;

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return getEmissionsMonitoringPlan() != null ?
            getEmissionsMonitoringPlan().getEmpSectionAttachmentIds() :
            Collections.emptySet();
    }
}