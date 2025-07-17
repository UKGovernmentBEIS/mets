package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#exist == (#certified != null)}", message = "emp.environmentalManagementSystem.exist")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#certified) == (#certificationStandard != null)}", message = "emp.environmentalManagementSystem.certified")
public class EmpEnvironmentalManagementSystem {

    private boolean exist;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean certified;

    @Size(max=250)
    private String certificationStandard;
}
