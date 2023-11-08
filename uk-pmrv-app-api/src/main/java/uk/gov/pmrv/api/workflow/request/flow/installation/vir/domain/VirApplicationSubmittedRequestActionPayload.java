package uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.Map;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VirApplicationSubmittedRequestActionPayload extends VirApplicationRequestActionPayload {

    @Valid
    @NotNull
    private VirVerificationData verificationData;

    @Valid
    @NotEmpty
    @Builder.Default
    private Map<String, @Valid @NotNull OperatorImprovementResponse> operatorImprovementResponses = new HashMap<>();
}
