package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(description = "Details of the permit notification used for the new cessation notifications")
public class PermitNotificationCompletedDecisionDetails extends PermitNotificationAcceptedDecisionDetails {

}
