package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.EnumMap;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ALRApplicationProceededToAuthorityRequestActionPayload extends RequestActionPayload {

    private ALR alr;

    private DecisionNotification decisionNotification;

    private ALRApplicationRegulatorReviewOutcome regulatorReviewOutcome;

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

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, RequestActionUserInfo> usersInfo = new HashMap<>();

    private FileInfoDTO officialNotice;

    @Override
    public Map<UUID, String> getAttachments() {
        Map<UUID, String> allAttachments = new HashMap<>();
        allAttachments.putAll(alrAttachments);
        allAttachments.putAll(regulatorReviewAttachments);
        allAttachments.putAll(verificationAttachments);
        return allAttachments;
    }

    @Override
    public Map<UUID, String> getFileDocuments() {
        if(officialNotice != null) {
            return Stream.of(super.getFileDocuments(),
                    Map.of(
                            UUID.fromString(officialNotice.getUuid()), officialNotice.getName()
                    )
            ).flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        return Collections.emptyMap();
    }
}
