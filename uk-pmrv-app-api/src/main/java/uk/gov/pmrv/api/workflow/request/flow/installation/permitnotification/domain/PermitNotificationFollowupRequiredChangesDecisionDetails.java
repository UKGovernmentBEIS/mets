package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import java.time.LocalDate;
import jakarta.validation.constraints.Future;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PermitNotificationFollowupRequiredChangesDecisionDetails extends ChangesRequiredDecisionDetails {

    @Future
    private LocalDate dueDate;
}
