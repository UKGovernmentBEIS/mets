package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterMonitoringTierDiffReason {

    @NotNull
    private ParameterMonitoringTierDiffReasonType type;

    @NotNull
    private String reason;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> relatedNotifications = new ArrayList<>();
}
