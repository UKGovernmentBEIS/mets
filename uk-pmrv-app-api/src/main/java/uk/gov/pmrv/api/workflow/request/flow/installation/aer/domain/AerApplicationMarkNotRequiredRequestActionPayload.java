package uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
public class AerApplicationMarkNotRequiredRequestActionPayload extends RequestActionPayload {

    @Valid
    @NotNull
    private AerMarkNotRequiredDetails markNotRequiredDetails;

}
