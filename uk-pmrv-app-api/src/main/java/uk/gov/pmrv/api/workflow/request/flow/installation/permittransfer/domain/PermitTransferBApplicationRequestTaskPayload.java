package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceReviewRequestTaskPayload;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitTransferBApplicationRequestTaskPayload extends PermitIssuanceReviewRequestTaskPayload {

    @NotNull
    @Valid
    private PermitTransferDetails permitTransferDetails;
    
    @NotNull
    @Valid
    private PermitTransferDetailsConfirmation permitTransferDetailsConfirmation;

    @Override
    public Set<UUID> getReferencedAttachmentIds() {

        final Set<UUID> reasonAttachmentIds = this.getPermitTransferDetails() != null ?
            this.getPermitTransferDetails().getReasonAttachments() : Set.of();

        return Stream.of(super.getReferencedAttachmentIds(), reasonAttachmentIds)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }
}
