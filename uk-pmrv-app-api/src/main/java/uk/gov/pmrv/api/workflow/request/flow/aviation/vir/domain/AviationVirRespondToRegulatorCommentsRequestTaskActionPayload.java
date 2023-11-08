package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain;

import jakarta.validation.constraints.NotBlank;
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
public abstract class AviationVirRespondToRegulatorCommentsRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotBlank
    private String reference;

    @Builder.Default
    private Map<String, Boolean> virRespondToRegulatorCommentsSectionsCompleted = new HashMap<>();
}
