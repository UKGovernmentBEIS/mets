package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain;

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
public class BDRVerificationReportDataRegulatorReviewDecision extends BDRReviewDecision {

    @NotNull
    private BDRVerificationReportDataRegulatorReviewDecisionType type;

    @Valid
    private BDRRegulatorReviewDecisionDetails details;
}
