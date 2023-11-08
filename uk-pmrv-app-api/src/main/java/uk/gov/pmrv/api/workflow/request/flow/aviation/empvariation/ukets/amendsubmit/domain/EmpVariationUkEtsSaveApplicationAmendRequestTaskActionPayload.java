package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsSaveApplicationReviewRequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class EmpVariationUkEtsSaveApplicationAmendRequestTaskActionPayload extends EmpVariationUkEtsSaveApplicationReviewRequestTaskActionPayload {

	private Boolean empVariationDetailsAmendCompleted;
}
