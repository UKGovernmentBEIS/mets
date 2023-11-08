package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain;

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
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsDetails;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmpVariationUkEtsApplicationSubmittedRequestActionPayload extends RequestActionPayload {

	@Valid
    @NotNull
    private EmissionsMonitoringPlanUkEts emissionsMonitoringPlan;

    @Valid
    @NotNull
    private ServiceContactDetails serviceContactDetails;
    
    @Valid
    @NotNull
    private EmpVariationUkEtsDetails empVariationDetails;

    @Builder.Default
    private Map<String, List<Boolean>> empSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> empAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getEmpAttachments();
    }
}
