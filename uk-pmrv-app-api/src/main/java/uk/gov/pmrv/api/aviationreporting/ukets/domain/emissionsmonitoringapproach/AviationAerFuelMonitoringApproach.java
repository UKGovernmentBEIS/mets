package uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class AviationAerFuelMonitoringApproach extends AviationAerEmissionsMonitoringApproach {
}
