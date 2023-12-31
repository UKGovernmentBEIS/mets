package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.common.ProcedureOptionalForm;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SamplingPlanDetails {

    @Valid
    @NotNull
    private ProcedureForm analysis;

    @Valid
    @NotNull
    private ProcedurePlan procedurePlan;
    
    @Valid
    @NotNull
    private ProcedureForm appropriateness;
    
    @Valid
    @NotNull
    private ProcedureOptionalForm yearEndReconciliation;
}
