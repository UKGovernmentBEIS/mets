package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ChangesRequiredDecisionDetails;

import java.time.LocalDate;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(description = "Details of the follow up decision details")
public class PermitNotificationFollowupRequiredChangesDecisionDetails extends ChangesRequiredDecisionDetails {

    @Future
    private LocalDate dueDate;
}
