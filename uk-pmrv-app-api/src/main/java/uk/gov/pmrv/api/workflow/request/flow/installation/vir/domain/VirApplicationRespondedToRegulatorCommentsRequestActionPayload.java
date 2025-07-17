package uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.common.reporting.verification.UncorrectedItem;
import uk.gov.pmrv.api.common.reporting.verification.VerifierComment;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementFollowUpResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{(#verifierUncorrectedItem != null) == (#verifierComment == null)}", message = "virApplicationRespondedToRegulatorCommentsRequestActionPayload.invalid")
public class VirApplicationRespondedToRegulatorCommentsRequestActionPayload extends VirApplicationRequestActionPayload {

    @Valid
    private UncorrectedItem verifierUncorrectedItem;

    @Valid
    private VerifierComment verifierComment;

    @Valid
    @NotNull
    private OperatorImprovementResponse operatorImprovementResponse;

    @Valid
    @NotNull
    private RegulatorImprovementResponse regulatorImprovementResponse;

    @Valid
    @NotNull
    private OperatorImprovementFollowUpResponse operatorImprovementFollowUpResponse;
}
