package uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain;

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
public class NERVerificationOpinionStatement {

    @NotNull
    private UUID opinionStatementFile;

    @Builder.Default
    private Set<UUID> supportingFiles = new HashSet<>();

    private String notes;
}
