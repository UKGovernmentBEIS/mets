package uk.gov.pmrv.api.workflow.request.flow.installation.air.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Year;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AirApplicationSubmittedRequestActionPayload extends RequestActionPayload {

    @NotEmpty
    @JsonDeserialize(as = LinkedHashMap.class)
    @Builder.Default
    private Map<Integer, @NotNull @Valid AirImprovement> airImprovements = new HashMap<>();

    @NotNull
    @PastOrPresent
    private Year reportingYear;

    @NotEmpty
    @Builder.Default
    private Map<Integer, @NotNull @Valid OperatorAirImprovementResponse> operatorImprovementResponses = new HashMap<>();


    @Builder.Default
    private Map<UUID, String> airAttachments = new HashMap<>();

    @Builder.Default
    private Map<Integer, Boolean> airSectionsCompleted = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getAirAttachments();
    }
}
