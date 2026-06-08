package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NERRegulatorReviewDecisionDetails {

    @Size(max = 10000)
    private String notes;
}
