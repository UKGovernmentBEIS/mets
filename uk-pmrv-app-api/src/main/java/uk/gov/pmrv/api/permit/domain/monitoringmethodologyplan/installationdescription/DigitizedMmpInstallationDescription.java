package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.installationdescription;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DigitizedMmpInstallationDescription {

    @NotBlank
    private String description;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(max = 10)
    private List<@Valid @NotNull InstallationConnection> connections = new ArrayList<>();

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> flowDiagrams = new HashSet<>();
}
