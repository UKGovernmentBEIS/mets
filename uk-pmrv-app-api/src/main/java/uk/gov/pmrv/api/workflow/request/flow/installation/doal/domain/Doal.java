package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doal {

    @Valid
    @NotNull
    private OperatorActivityLevelReport operatorActivityLevelReport;

    @Valid
    @NotNull
    private VerificationReportOfTheActivityLevelReport verificationReportOfTheActivityLevelReport;

    @Valid
    @NotNull
    private DoalAdditionalDocuments additionalDocuments;

    @Valid
    @NotNull
    private ActivityLevelChangeInformation activityLevelChangeInformation;

    @Valid
    @NotNull
    private DoalDetermination determination;
}
