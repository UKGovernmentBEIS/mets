package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestActionSubmittedToAware;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NonCompliancePeerReviewRequestedRequestActionPayload extends RequestActionPayload
        implements RequestActionSubmittedToAware {

    private String submittedTo;
}
