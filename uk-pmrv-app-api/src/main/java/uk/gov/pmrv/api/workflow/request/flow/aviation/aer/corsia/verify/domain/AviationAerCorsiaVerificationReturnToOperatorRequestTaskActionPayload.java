package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
public class AviationAerCorsiaVerificationReturnToOperatorRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    @NotEmpty
    private String changesRequired;
}
