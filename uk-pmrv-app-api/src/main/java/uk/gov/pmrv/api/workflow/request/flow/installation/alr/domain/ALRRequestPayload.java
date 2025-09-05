package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.HashMap;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ALRRequestPayload extends RequestPayload {

    private ALR alr;

    private DecisionNotification decisionNotification;

    private ALRApplicationRegulatorReviewOutcome regulatorReviewOutcome;

    private ALRApplicationAuthorityReviewOutcome authorityReviewOutcome;

    @Builder.Default
    private Map<String, Boolean> regulatorReviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<ALRReviewGroup, ALRReviewDecision> regulatorReviewGroupDecisions = new EnumMap<>(ALRReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> regulatorReviewAttachments = new HashMap<>();

    private boolean verificationPerformed;

    private ALRVerificationReport verificationReport;

    @Builder.Default
    private Map<UUID, String> verificationAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, List<Boolean>> verificationSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> alrAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> alrSectionsCompleted = new HashMap<>();

    @Builder.Default
    private int alrFileVersion = 1;

    private FileInfoDTO officialNotice;

    @JsonIgnore
    public ALRVerificationData getVerificationData() {
        return verificationReport == null ? null : verificationReport.getVerificationData();
    }

    public void incrementAlrFileVersion() {
        this.alrFileVersion += 1;
    }
}
