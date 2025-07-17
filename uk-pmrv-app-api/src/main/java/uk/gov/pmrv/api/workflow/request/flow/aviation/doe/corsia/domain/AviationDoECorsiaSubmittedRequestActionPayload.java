package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationDoECorsiaSubmittedRequestActionPayload extends RequestActionPayload {

    @Valid
    @NotNull
    private AviationDoECorsia doe;

    private boolean sectionCompleted;

    @Builder.Default
    private Map<UUID, String> doeAttachments = new HashMap<>();

    @Valid
    @NotNull
    private DecisionNotification decisionNotification;

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, RequestActionUserInfo> usersInfo = new HashMap<>();

    private FileInfoDTO officialNotice;

    @Override
    public Map<UUID, String> getAttachments() {
        return getDoeAttachments();
    }

    @Override
    public Map<UUID, String> getFileDocuments() {
        Map<UUID, String> officialNoticeFileDocuments = Map.of(UUID.fromString(officialNotice.getUuid()), officialNotice.getName());

        return Stream.of(super.getFileDocuments(),officialNoticeFileDocuments)
            .flatMap(m -> m.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
