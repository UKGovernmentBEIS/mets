package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload
    extends PermitNotificationFollowUpApplicationReviewRequestTaskPayload {

    @Builder.Default
    private Map<String, Boolean> followUpSectionsCompleted = new HashMap<>();
}
