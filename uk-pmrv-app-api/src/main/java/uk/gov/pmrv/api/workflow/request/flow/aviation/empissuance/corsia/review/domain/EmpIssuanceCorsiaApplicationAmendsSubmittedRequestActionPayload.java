package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
public class EmpIssuanceCorsiaApplicationAmendsSubmittedRequestActionPayload extends
    EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload {
}
