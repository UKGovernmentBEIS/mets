package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(description = "Details of the permit notification used accept requests of regulators")
public class PermitNotificationAcceptedDecisionDetails extends PermitNotificationReviewDecisionDetails {

    @Valid
    @NotNull(message = "permitNotification.reviewDecision.followUp")
    private FollowUp followUp;

}
