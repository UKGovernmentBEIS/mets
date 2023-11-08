package uk.gov.pmrv.api.workflow.request.flow.installation.air.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.NoHighestRequiredTierJustification;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OperatorAirImprovementNoResponse extends OperatorAirImprovementResponse {

    @JsonUnwrapped
    @Valid
    private NoHighestRequiredTierJustification noHighestRequiredTierJustification;
    
    @JsonIgnore
    @Override
    public Set<UUID> getAirFiles() {
        return noHighestRequiredTierJustification != null ? noHighestRequiredTierJustification.getFiles() : new HashSet<>();
    }
}
