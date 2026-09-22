package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PermitVariationRequestPaymentActionPayload extends RequestActionPayload {

    @Valid
    @NotNull
    private PermitVariationRequestPaymentDetails paymentDetails;
}
