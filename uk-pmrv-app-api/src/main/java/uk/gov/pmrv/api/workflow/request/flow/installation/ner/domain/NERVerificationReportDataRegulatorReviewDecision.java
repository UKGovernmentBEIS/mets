package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERVerificationReportDataRegulatorReviewDecisionType;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NERVerificationReportDataRegulatorReviewDecision extends NERReviewDecision {

    @NotNull
    private NERVerificationReportDataRegulatorReviewDecisionType type;

    @Valid
    private NERRegulatorReviewDecisionDetails details;
}
