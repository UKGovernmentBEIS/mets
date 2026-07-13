package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsApplicationSubmittedRequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class EmpVariationUkEtsApplicationAmendsSubmittedRequestActionPayload extends EmpVariationUkEtsApplicationSubmittedRequestActionPayload {
}
