package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class FuelMonitoringApproach extends EmpEmissionsMonitoringApproach {
}
