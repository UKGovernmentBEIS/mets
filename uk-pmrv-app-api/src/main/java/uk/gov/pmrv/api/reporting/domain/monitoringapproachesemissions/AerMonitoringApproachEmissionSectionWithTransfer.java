package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public abstract class AerMonitoringApproachEmissionSectionWithTransfer extends AerMonitoringApproachEmissions {
    private boolean hasTransfer;
}
