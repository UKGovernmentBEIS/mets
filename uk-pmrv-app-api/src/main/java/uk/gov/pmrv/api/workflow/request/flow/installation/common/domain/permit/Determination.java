package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Determination implements Determinateable {

    private DeterminationType type;

    @NotBlank
    @Size(max = 10000)
    private String reason;
}
