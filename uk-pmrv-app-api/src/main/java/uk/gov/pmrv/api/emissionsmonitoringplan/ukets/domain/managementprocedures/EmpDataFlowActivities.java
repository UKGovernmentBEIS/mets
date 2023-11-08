package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmpDataFlowActivities extends EmpProcedureForm {

    @Size(max = 250)
    private String diagramReference;

    @Size(max = 250)
    private String otherStandardsApplied;

    @NotBlank
    @Size(max=1000)
    private String primaryDataSources;

    @NotBlank
    @Size(max = 10000)
    private String processingSteps;

    @NotNull
    private UUID diagramAttachmentId;
}
