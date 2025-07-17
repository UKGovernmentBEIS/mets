package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ALRVerificationData {

    @NotNull
    @Valid
    private ALRVerificationOpinionStatement opinionStatement;

    @NotNull
    @Valid
    private ALROverallVerificationAssessment overallAssessment;
}
