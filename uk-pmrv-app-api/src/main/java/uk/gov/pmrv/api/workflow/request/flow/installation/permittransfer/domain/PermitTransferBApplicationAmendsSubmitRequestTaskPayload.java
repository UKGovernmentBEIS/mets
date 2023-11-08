package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationAmendsSubmitRequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitTransferBApplicationAmendsSubmitRequestTaskPayload extends PermitIssuanceApplicationAmendsSubmitRequestTaskPayload {
    
    private PermitTransferDetails permitTransferDetails;

    private PermitTransferDetailsConfirmation permitTransferDetailsConfirmation;
}
