package uk.gov.pmrv.api.workflow.request.flow.installation.air.domain;

import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AirSubmitRespondToRegulatorCommentsRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    private Integer reference;

    @Builder.Default
    private Map<Integer, Boolean> airRespondToRegulatorCommentsSectionsCompleted = new HashMap<>();
}
