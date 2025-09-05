package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ALRApplicationWaitForAuthorityReviewRequestTaskPayload extends RequestTaskPayload {
}
