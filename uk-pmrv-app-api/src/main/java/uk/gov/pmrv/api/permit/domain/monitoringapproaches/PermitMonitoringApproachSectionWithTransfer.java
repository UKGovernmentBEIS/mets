package uk.gov.pmrv.api.permit.domain.monitoringapproaches;

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
public abstract class PermitMonitoringApproachSectionWithTransfer extends PermitMonitoringApproachSection {
    private boolean hasTransfer;
}
