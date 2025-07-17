package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ALRVerifiedWithCommentsOverallVerificationAssessment extends ALROverallVerificationAssessment{

    @NotBlank
    @Size(max = 10000)
    private String reasons;
}
