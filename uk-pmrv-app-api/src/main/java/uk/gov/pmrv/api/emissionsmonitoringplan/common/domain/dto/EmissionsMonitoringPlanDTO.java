package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanContainer;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class EmissionsMonitoringPlanDTO<T extends EmissionsMonitoringPlanContainer> {

    // This DTO is going to have all the necessary fields from EMP entity

    private String id;

    private Long accountId;

    private int consolidationNumber;
    
    public abstract T getEmpContainer();

}
