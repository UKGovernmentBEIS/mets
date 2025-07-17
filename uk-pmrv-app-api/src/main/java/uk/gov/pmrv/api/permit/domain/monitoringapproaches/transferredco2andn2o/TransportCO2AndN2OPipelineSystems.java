package uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2andn2o;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.common.ProcedureOptionalForm;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#exist) == (#procedureForLeakageEvents != null)}", message = "permit.procedureOptionalForm.exist")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#exist) == (#temperaturePressure != null)}", message = "permit.transferredCO2MonitoringApproach.temperaturePressure.exist")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#exist) == (#proceduresForTransferredCO2AndN2O != null)}", message = "permit.procedureOptionalForm.exist")
public class TransportCO2AndN2OPipelineSystems {

    private Boolean exist;

    @Valid
    private ProcedureOptionalForm procedureForLeakageEvents;

    @Valid
    private TemperaturePressure temperaturePressure;

    @Valid
    private ProcedureForm proceduresForTransferredCO2AndN2O;
}
