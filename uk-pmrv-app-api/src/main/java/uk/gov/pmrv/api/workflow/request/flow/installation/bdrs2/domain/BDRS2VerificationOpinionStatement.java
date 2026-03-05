package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain;

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
public class BDRS2VerificationOpinionStatement {

    @Builder.Default
    private Set<UUID> opinionStatementFiles = new HashSet<>();

    private String notes;

}
