package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WasteQDRRegulatorReviewRequiredChange {
    @Size(max = 10000)
    @NotBlank
    private String reason;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();

}
