package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PFCSourceStreamCategoryAppliedTier {
    
    @Valid
    @NotNull
    private PFCSourceStreamCategory sourceStreamCategory;

    @Valid
    @NotNull
    private PFCActivityData activityData;
    
    @Valid
    @NotNull
    private PFCEmissionFactor emissionFactor;
}
