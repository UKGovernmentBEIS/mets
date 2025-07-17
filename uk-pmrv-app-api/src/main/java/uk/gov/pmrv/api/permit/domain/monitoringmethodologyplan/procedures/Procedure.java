package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.procedures;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Procedure {

    @NotBlank
    private String procedureName;

    @NotBlank
    private String procedureReference;

    private String diagramReference;

    private String procedureDescription;

    private String dataMaintenanceResponsibleEntity;

    private String locationOfRecords;

    private String itSystemUsed;

    private String standardsAppliedList;




}
