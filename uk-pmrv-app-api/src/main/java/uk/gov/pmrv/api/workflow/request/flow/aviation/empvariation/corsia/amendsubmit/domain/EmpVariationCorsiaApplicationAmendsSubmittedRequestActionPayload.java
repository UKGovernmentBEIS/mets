package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationSubmittedRequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
public class EmpVariationCorsiaApplicationAmendsSubmittedRequestActionPayload extends
    EmpVariationCorsiaApplicationSubmittedRequestActionPayload {
}
