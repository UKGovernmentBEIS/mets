package uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AviationAccountUpdatedRegistryEvent {

    private Long accountId;
    private String requestId;
    private EmissionsMonitoringPlanUkEts emissionsMonitoringPlan;
    private boolean isFromSetOperatorId;

}
