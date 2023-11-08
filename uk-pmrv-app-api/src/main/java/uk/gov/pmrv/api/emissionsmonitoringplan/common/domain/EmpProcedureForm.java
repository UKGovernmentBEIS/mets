package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain;

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
public class EmpProcedureForm {

    @NotBlank
    @Size(max = 10000)
    private String procedureDescription;

    @NotBlank
    @Size(max = 500)
    private String procedureDocumentName;

    @NotBlank
    @Size(max = 500)
    private String procedureReference;

    @NotBlank
    @Size(max = 500)
    private String responsibleDepartmentOrRole;

    @NotBlank
    @Size(max = 500)
    private String locationOfRecords;

    @Size(max = 500)
    private String itSystemUsed;

}
