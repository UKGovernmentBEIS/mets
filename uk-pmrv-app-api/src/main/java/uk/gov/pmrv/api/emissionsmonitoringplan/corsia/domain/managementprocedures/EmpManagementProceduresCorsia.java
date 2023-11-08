package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmpCorsiaSection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpManagementProceduresCorsia implements EmpCorsiaSection {

    @Valid
    @NotNull
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EmpMonitoringReportingRolesCorsia monitoringReportingRoles;

    @Valid
    @NotNull
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EmpDataManagement dataManagement;

    @Valid
    @NotNull
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EmpProcedureDescription recordKeepingAndDocumentation;

    @Valid
    @NotNull
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EmpProcedureDescription riskExplanation;

    @Valid
    @NotNull
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private EmpProcedureDescription empRevisions;

    @JsonIgnore
    public Set<UUID> getAttachmentIds(){
        Set<UUID> attachments = new HashSet<>();
        if(dataManagement != null && dataManagement.getDataFlowDiagram() != null) {
            attachments.add(dataManagement.getDataFlowDiagram());
        }
        return Collections.unmodifiableSet(attachments);
    }
}
