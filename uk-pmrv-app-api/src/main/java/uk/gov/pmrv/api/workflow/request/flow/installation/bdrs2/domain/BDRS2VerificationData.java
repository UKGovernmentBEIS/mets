package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BDRS2VerificationData {


    @NotNull
    @Valid
    private BDRS2VerificationOpinionStatement opinionStatement;

    @NotNull
    @Valid
    private BDRS2OverallVerificationAssessment overallAssessment;
}
