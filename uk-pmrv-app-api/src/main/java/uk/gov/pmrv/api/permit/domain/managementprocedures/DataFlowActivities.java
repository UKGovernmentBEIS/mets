package uk.gov.pmrv.api.permit.domain.managementprocedures;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DataFlowActivities extends ManagementProceduresDefinition {

    @NotBlank
    @Size(max=10000)
    private String primaryDataSources;

    @NotBlank
    @Size(max = 10000)
    private String processingSteps;

    private UUID diagramAttachmentId;

}
