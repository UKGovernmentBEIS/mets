package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.submit.domain.PermitIssuanceApplicationSubmittedRequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitIssuanceApplicationGrantedRequestActionPayload extends
    PermitIssuanceApplicationSubmittedRequestActionPayload {

    @Valid
    @NotNull
    private DecisionNotification decisionNotification;

    @Builder.Default
    private Map<PermitReviewGroup, PermitIssuanceReviewDecision> reviewGroupDecisions = new EnumMap<>(PermitReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    @NotNull
    private PermitIssuanceGrantDetermination determination;

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, RequestActionUserInfo> usersInfo = new HashMap<>();

    @NotNull
    private FileInfoDTO officialNotice;

    @NotNull
    private FileInfoDTO permitDocument;
    
    @Override
    public Map<UUID, String> getFileDocuments() {
        return Stream.of(super.getFileDocuments(),
                Map.of(
                    UUID.fromString(officialNotice.getUuid()), officialNotice.getName(),
                    UUID.fromString(permitDocument.getUuid()), permitDocument.getName()
                )
            )
            .flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
