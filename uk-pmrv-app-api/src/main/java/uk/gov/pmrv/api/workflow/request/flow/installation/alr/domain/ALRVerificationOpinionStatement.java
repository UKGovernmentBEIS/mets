package uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain;

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
public class ALRVerificationOpinionStatement {

    @Builder.Default
    private Set<UUID> opinionStatementFiles = new HashSet<>();

    @Builder.Default
    private Set<UUID> supportingFiles = new HashSet<>();

    private String notes;
}
