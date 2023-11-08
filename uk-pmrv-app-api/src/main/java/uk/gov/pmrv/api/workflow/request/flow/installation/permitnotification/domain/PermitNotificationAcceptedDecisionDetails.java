package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

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
public class PermitNotificationAcceptedDecisionDetails extends PermitNotificationReviewDecisionDetails {

    @Valid
    @NotNull(message = "permitNotification.reviewDecision.followUp")
    private FollowUp followUp;

}
