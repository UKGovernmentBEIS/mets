package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaSaveApplicationReviewRequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class EmpVariationCorsiaSaveApplicationAmendRequestTaskActionPayload extends
	EmpVariationCorsiaSaveApplicationReviewRequestTaskActionPayload {

	private Boolean empVariationDetailsAmendCompleted;
}
