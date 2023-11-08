package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsApplicationSubmittedRequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
public class EmpVariationUkEtsApplicationAmendsSubmittedRequestActionPayload extends EmpVariationUkEtsApplicationSubmittedRequestActionPayload {
}
