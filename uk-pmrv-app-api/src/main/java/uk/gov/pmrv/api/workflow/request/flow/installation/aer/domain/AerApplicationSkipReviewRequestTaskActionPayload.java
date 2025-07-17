package uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AerApplicationSkipReviewRequestTaskActionPayload extends RequestTaskActionPayload {


        @Valid
        @JsonUnwrapped
        private AerSkipReviewDecision aerSkipReviewDecision;
}
