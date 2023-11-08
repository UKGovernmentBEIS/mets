package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringApproachEmissions {

    @Valid
    @NotEmpty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private Map<MonitoringApproachType, AerMonitoringApproachEmissions> monitoringApproachEmissions = new EnumMap<>(MonitoringApproachType.class);

    @JsonIgnore
    public Set<UUID> getAttachmentIds(){
        return monitoringApproachEmissions.values().stream()
                .map(AerMonitoringApproachEmissions::getAttachmentIds)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
}
