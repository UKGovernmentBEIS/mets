package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

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

import java.time.Year;
import java.util.Collections;
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
public class DoalApplicationProceededToAuthorityRequestActionPayload extends RequestActionPayload {

    @NotNull
    private Year reportingYear;

    @Valid
    @NotNull
    private Doal doal;

    @Valid
    private DecisionNotification decisionNotification;

    @Builder.Default
    private Map<UUID, String> doalAttachments = new HashMap<>();

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, RequestActionUserInfo> usersInfo = new HashMap<>();

    private FileInfoDTO officialNotice;

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getDoalAttachments();
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
