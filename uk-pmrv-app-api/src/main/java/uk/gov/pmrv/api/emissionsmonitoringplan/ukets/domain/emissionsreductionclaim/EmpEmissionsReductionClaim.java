package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsreductionclaim;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmpUkEtsSection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "#exist == (#safMonitoringSystemsAndProcesses != null)",
    message = "emp.emissionsReductionClaim.exist.safMonitoringSystemsAndProcesses")
@SpELExpression(expression = "#exist == (#rtfoSustainabilityCriteria != null)",
    message = "emp.emissionsReductionClaim.exist.rtfoSustainabilityCriteria")
@SpELExpression(expression = "#exist == (#safDuplicationPrevention != null)",
    message = "emp.emissionsReductionClaim.exist.safDuplicationPrevention")
public class EmpEmissionsReductionClaim implements EmpUkEtsSection {

    private boolean exist;

    @Valid
    private EmpProcedureForm safMonitoringSystemsAndProcesses;

    @Valid
    private EmpProcedureForm rtfoSustainabilityCriteria;

    @Valid
    private EmpProcedureForm safDuplicationPrevention;
}
