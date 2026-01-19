package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

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
public class WasteQDRApplicationSubmittedRequestActionPayload extends RequestActionPayload {

    @Valid
    @NotNull
    private WasteQDR qdr;

    @Valid
    @NotNull
    private InstallationOperatorDetails installationOperatorDetails;

    @Builder.Default
    private Map<UUID, String> wasteQDRAttachments = new HashMap<>();


    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(wasteQDRAttachments)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
