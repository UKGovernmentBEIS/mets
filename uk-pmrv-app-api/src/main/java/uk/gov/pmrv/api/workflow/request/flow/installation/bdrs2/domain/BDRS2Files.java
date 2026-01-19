package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BDRS2Files {

    @NotNull
    private UUID file;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingFiles = new HashSet<>();
}
