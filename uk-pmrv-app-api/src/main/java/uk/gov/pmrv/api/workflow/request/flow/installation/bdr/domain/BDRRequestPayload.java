package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayloadVerifiable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class BDRRequestPayload  extends RequestPayload  implements RequestPayloadVerifiable<BDRVerificationReport> {

    private BDR bdr;

    @NotNull
    @Valid
    private BDRApplicationRegulatorReviewOutcome regulatorReviewOutcome;

    @Builder.Default
    private Map<String, Boolean> regulatorReviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<BDRReviewGroup, BDRReviewDecision> regulatorReviewGroupDecisions = new EnumMap<>(BDRReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> regulatorReviewAttachments = new HashMap<>();

    private boolean verificationPerformed;

    private BDRVerificationReport verificationReport;

    @Builder.Default
    private Map<UUID, String> verificationAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, List<Boolean>> verificationSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> bdrAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> bdrSectionsCompleted = new HashMap<>();

    @JsonIgnore
    public BDRVerificationData getVerificationData() {
        return verificationReport == null ? null : verificationReport.getVerificationData();
    }
}
