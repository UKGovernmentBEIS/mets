package uk.gov.pmrv.api.workflow.request.flow.rde.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
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
public class RdeRejectedRequestActionPayload extends RequestActionPayload {

    private final RdeDecisionType decision = RdeDecisionType.REJECTED;
    
    @NotBlank
    private String reason;
}
