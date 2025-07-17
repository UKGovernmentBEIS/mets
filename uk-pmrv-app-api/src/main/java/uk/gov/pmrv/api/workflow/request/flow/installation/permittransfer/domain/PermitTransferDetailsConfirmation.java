package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#detailsAccepted) == (#detailsRejectedReason != null)}", 
    message = "permit.transfer.details.confirmation.details.not.compatible.with.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#regulatedActivitiesInOperation) == (#regulatedActivitiesNotInOperationReason != null)}", 
    message = "permit.transfer.details.confirmation.regulated.activities.not.compatible.with.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#transferAccepted) == (#transferRejectedReason != null)}", 
    message = "permit.transfer.details.confirmation.transfer.not.compatible.with.reason")
public class PermitTransferDetailsConfirmation {

    @NotNull
    private Boolean detailsAccepted;

    @Size(max = 10000)
    private String detailsRejectedReason;

    @NotNull
    private Boolean regulatedActivitiesInOperation;

    @Size(max = 10000)
    private String regulatedActivitiesNotInOperationReason;

    @NotNull
    private Boolean transferAccepted;

    @Size(max = 10000)
    private String transferRejectedReason;
}
