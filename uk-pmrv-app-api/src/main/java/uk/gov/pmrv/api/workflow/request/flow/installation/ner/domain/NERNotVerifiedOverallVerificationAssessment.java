package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class NERNotVerifiedOverallVerificationAssessment extends NEROverallVerificationAssessment {

    @NotBlank
    @Size(max = 10000)
    private String reasons;
}
