package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmissionsMonitoringPlanDTO;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmissionsMonitoringPlanCorsiaDTO extends EmissionsMonitoringPlanDTO<EmissionsMonitoringPlanCorsiaContainer> {

    private EmissionsMonitoringPlanCorsiaContainer empContainer;
    
}
