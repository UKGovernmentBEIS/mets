package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.PermitSection;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringMethodologyPlans implements PermitSection {

    private boolean exist;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private Set<UUID> plans = new HashSet<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Valid
    private DigitizedPlan digitizedPlan;

}