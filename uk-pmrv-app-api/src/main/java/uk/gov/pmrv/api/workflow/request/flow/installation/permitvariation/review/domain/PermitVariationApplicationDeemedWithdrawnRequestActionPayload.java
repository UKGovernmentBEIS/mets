package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitVariationApplicationDeemedWithdrawnRequestActionPayload extends RequestActionPayload {

    @Valid
    @NotNull
    private DecisionNotification decisionNotification;

    @Valid
    @NotNull
    private PermitVariationDeemedWithdrawnDetermination determination;

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, RequestActionUserInfo> usersInfo = new HashMap<>();
    
    @NotNull
    private FileInfoDTO officialNotice;
    
    @Override
    public Map<UUID, String> getFileDocuments() {
        return Stream.of(super.getFileDocuments(),
                Map.of(
                    UUID.fromString(officialNotice.getUuid()), officialNotice.getName()
                )
            )
            .flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
}
