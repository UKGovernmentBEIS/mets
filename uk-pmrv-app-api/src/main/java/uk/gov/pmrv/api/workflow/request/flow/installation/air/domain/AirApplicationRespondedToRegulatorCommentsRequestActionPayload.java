package uk.gov.pmrv.api.workflow.request.flow.installation.air.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AirApplicationRespondedToRegulatorCommentsRequestActionPayload extends RequestActionPayload {

    @NotNull
    @PastOrPresent
    private Year reportingYear;
    
    @NotNull
    private Integer reference;
    
    @Valid
    @NotNull
    private AirImprovement airImprovement;

    @Valid
    @NotNull
    private OperatorAirImprovementResponse operatorImprovementResponse;

    @NotNull
    private RegulatorAirImprovementResponse regulatorImprovementResponse;

    @Valid
    @NotNull
    private OperatorAirImprovementFollowUpResponse operatorImprovementFollowUpResponse;

    @Builder.Default
    private Map<UUID, String> airAttachments = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        
        return Stream.of(this.getAirAttachments(), this.getReviewAttachments())
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
