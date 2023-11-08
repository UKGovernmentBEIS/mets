package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.permit.domain.PermitType;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermitOriginatedData {

    @Valid
    @NotNull
    private MonitoringApproachMonitoringTiers permitMonitoringApproachMonitoringTiers;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private List<String> permitNotificationIds = new ArrayList<>();

    @NotNull
    private PermitType permitType;

    @NotNull
    private InstallationCategory installationCategory;
}
