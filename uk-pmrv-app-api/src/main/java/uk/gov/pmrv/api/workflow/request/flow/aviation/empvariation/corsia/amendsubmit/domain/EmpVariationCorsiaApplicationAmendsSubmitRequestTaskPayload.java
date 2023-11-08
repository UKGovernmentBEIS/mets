package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class EmpVariationCorsiaApplicationAmendsSubmitRequestTaskPayload extends
	EmpVariationCorsiaApplicationReviewRequestTaskPayload {

	private Boolean empVariationDetailsAmendCompleted;
}
