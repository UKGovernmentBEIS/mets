package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BDRS2VerificationReportDataRegulatorReviewDecision extends BDRS2ReviewDecision {

    @NotNull
    private BDRS2VerificationReportDataRegulatorReviewDecisionType type;

    @Valid
    private BDRS2RegulatorReviewDecisionDetails details;
}
