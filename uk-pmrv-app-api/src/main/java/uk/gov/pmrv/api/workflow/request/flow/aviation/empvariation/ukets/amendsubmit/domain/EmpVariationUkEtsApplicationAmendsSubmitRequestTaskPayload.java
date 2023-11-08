package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class EmpVariationUkEtsApplicationAmendsSubmitRequestTaskPayload extends EmpVariationUkEtsApplicationReviewRequestTaskPayload {

	private Boolean empVariationDetailsAmendCompleted;
}
