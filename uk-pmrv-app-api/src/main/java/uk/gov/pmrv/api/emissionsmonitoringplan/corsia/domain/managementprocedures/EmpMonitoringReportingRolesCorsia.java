package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.managementprocedures;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpMonitoringReportingRolesCorsia {

    @Valid
    @NotEmpty
    @Builder.Default
    private List<EmpMonitoringReportingRoleCorsia> monitoringReportingRoles = new ArrayList<>();
}
