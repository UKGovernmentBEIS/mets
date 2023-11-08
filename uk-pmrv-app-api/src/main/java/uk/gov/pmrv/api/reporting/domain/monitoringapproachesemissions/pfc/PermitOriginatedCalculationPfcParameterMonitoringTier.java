package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.ActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCEmissionFactorTier;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PermitOriginatedCalculationPfcParameterMonitoringTier {
    private boolean massBalanceApproachUsed;

    @NotNull
    private PFCEmissionFactorTier emissionFactorTier;

    @NotNull
    private ActivityDataTier activityDataTier;
}
