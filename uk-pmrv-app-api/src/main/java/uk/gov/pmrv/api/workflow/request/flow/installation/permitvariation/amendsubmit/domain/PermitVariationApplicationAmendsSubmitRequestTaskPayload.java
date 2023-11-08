package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class PermitVariationApplicationAmendsSubmitRequestTaskPayload extends PermitVariationApplicationReviewRequestTaskPayload {

	private Boolean permitVariationDetailsAmendCompleted;
}
