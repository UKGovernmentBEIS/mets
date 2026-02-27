package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(
        expression = "{(#bdrs2guardQuestions.continueApplicationForFreeAllocationType != 'WITHDRAW' and #bdrs2Files != null) or " +
                "(#bdrs2guardQuestions.continueApplicationForFreeAllocationType == 'WITHDRAW' and #bdrs2Files == null)}",
        message = "bdrs2.files.bdrs2.required"
)
@SpELExpression(
        expression = "{(#bdrs2guardQuestions.continueApplicationForFreeAllocationType != 'WITHDRAW' and #bdrs2guardQuestions.inEiteSector == true " +
                "and #bdrs2guardQuestions.requiresAdditionalSubInstallationSplitsForCbam == true " +
                "and #mmpFiles != null) or " +
                "((#bdrs2guardQuestions.continueApplicationForFreeAllocationType == 'WITHDRAW' " +
                "or #bdrs2guardQuestions.inEiteSector != true " +
                "or #bdrs2guardQuestions.requiresAdditionalSubInstallationSplitsForCbam != true) " +
                "and #mmpFiles == null)}",
        message = "bdrs2.files.mmp.required"
)
public class BDRS2 {

    @NotNull
    //TODO: add @Valid
    private BDRS2GuardQuestions bdrs2guardQuestions;

    @Valid
    private BDRS2Files bdrs2Files;

    @Valid
    private BDRS2Files mmpFiles;
}
