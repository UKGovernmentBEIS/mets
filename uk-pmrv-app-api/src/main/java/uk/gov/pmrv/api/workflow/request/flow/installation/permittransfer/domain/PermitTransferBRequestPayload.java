package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPayloadPayable;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class PermitTransferBRequestPayload extends PermitIssuanceRequestPayload implements RequestPayloadPayable {
    
    private PermitTransferDetails permitTransferDetails;

    private PermitTransferDetailsConfirmation permitTransferDetailsConfirmation;

    private PermitTransferBDetailsConfirmationReviewDecision permitTransferDetailsConfirmationDecision;
    
    private String relatedRequestId;
}
