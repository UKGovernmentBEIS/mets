package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanContainer;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmissionsMonitoringPlanUkEtsContainer extends EmissionsMonitoringPlanContainer {

    @Valid
    @NotNull
    private EmissionsMonitoringPlanUkEts emissionsMonitoringPlan;

    @Valid
    @NotNull
    private ServiceContactDetails serviceContactDetails;
}
