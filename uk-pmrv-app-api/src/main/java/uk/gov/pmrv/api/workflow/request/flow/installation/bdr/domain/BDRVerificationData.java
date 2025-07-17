package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain;

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
public class BDRVerificationData {


    @NotNull
    @Valid
    private BDRVerificationOpinionStatement opinionStatement;

    @NotNull
    @Valid
    private BDROverallVerificationAssessment overallAssessment;
}
