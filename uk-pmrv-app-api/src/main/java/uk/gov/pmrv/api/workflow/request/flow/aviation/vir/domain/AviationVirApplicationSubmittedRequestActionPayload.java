package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.OperatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.VirVerificationData;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationVirApplicationSubmittedRequestActionPayload extends RequestActionPayload {

    @NotNull
    @PastOrPresent
    private Year reportingYear;

    @Valid
    @NotNull
    private VirVerificationData verificationData;

    @Valid
    @NotEmpty
    @Builder.Default
    private Map<String, @Valid @NotNull OperatorImprovementResponse> operatorImprovementResponses = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> virAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getVirAttachments();
    }
}
