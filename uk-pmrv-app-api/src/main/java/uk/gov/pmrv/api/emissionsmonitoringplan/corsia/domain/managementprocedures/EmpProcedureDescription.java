package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EmpProcedureDescription {

    @NotBlank
    @Size(max = 10000)
    private String description;
}
