package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistryIntegrationCreateRequestActionDTO {

    private String requestId;
    private AviationAccountDTO account;
    private EmissionsMonitoringPlanUkEts emissionsMonitoringPlanUkEts;
    private String empId;
    private AppUser appUser;

}
