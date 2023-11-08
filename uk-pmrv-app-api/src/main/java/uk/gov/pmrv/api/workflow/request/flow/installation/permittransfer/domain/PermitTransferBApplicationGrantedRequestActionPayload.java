package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationGrantedRequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitTransferBApplicationGrantedRequestActionPayload extends PermitIssuanceApplicationGrantedRequestActionPayload {
    
    @Valid
    @NotNull
    private PermitTransferDetails permitTransferDetails;

    @Valid
    @NotNull
    private PermitTransferDetailsConfirmation permitTransferDetailsConfirmation;

    @Valid
    @NotNull
    private PermitTransferBDetailsConfirmationReviewDecision permitTransferDetailsConfirmationDecision;
}
