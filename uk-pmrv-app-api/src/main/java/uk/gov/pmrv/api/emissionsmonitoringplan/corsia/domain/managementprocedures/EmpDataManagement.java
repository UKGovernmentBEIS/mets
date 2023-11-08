package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmpDataManagement extends EmpProcedureDescription{

    @NotNull
    private UUID dataFlowDiagram;
}
