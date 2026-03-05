package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstallationInspectionDetails {

    private LocalDate date;

    @Builder.Default
    @NotEmpty
    @Size(max = 5)
    private List<@NotBlank @Size(max = 255) String> officerNames = new ArrayList<>();

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> regulatorExtraFiles = new HashSet<>();

    @Size(max = 255)
    private String additionalInformation;

}
