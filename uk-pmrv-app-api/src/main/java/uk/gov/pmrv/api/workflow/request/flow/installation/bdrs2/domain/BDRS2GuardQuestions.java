package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// TODO: should we validate if type != WITHDRAW then applicationWithdrawalReason should be null?
@SpELExpression(expression = "{#continueApplicationForFreeAllocationType ne 'WITHDRAW' || #applicationWithdrawalReason != null}", message = "bdrs2.guardQuestions.applicationWithdrawalReason")
@SpELExpression(expression = "{#inEiteSector ne true || #requiresAdditionalSubInstallationSplitsForCbam != null}", message = "bdrs2.guardQuestions.requiresAdditionalSubInstallationSplitsForCbam")
public class BDRS2GuardQuestions {

    @NotNull(message = "bdrs2.guardQuestions.continueApplicationForFreeAllocationType")
    private BDRS2ContinueApplicationForFreeAllocationType continueApplicationForFreeAllocationType;

    @Size(max=10000)
    private String applicationWithdrawalReason;

    @NotNull(message = "bdrs2.guardQuestions.covidAdjustments")
    private Boolean covidAdjustments;

    // Emissions/Energy Intensive Trade Exposed (EITE)
    @NotNull(message = "bdrs2.guardQuestions.inEiteSector")
    private Boolean inEiteSector;

    // UK Carbon Border Adjustment Mechanism (CBAM)
    private Boolean requiresAdditionalSubInstallationSplitsForCbam;
}
