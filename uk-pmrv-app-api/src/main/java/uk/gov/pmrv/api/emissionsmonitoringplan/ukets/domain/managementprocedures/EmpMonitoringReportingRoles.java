package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpMonitoringReportingRoles {

    @Valid
    @NotEmpty
    @Builder.Default
    private List<EmpMonitoringReportingRole> monitoringReportingRoles = new ArrayList<>();
}
