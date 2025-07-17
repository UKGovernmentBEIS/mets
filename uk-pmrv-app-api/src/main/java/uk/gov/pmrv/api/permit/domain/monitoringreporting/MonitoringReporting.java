package uk.gov.pmrv.api.permit.domain.monitoringreporting;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.PermitSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringReporting implements PermitSection {

    @NotEmpty
    @Valid
    @Builder.Default
    private List<MonitoringRole> monitoringRoles = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> organisationCharts;
}
