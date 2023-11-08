package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmpVariationCorsiaApplicationSubmittedRequestActionPayload extends RequestActionPayload {

	@Valid
    @NotNull
    private EmissionsMonitoringPlanCorsia emissionsMonitoringPlan;

    @Valid
    @NotNull
    private ServiceContactDetails serviceContactDetails;
    
    @Valid
    @NotNull
    private EmpVariationCorsiaDetails empVariationDetails;

    @Builder.Default
    private Map<String, List<Boolean>> empSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> empAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getEmpAttachments();
    }
}
