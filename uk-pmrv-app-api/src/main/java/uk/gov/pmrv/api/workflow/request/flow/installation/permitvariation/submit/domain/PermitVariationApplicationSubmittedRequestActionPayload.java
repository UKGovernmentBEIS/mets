package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationDetails;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitVariationApplicationSubmittedRequestActionPayload extends RequestActionPayload {

    @NotNull
    private PermitType permitType;

    @Valid
    @NotNull
    private InstallationOperatorDetails installationOperatorDetails;

    @Valid
    @NotNull
    private Permit permit;
    
    @NotNull
	private PermitVariationDetails permitVariationDetails;

    @Builder.Default
    private Map<UUID, String> permitAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, List<Boolean>> permitSectionsCompleted = new HashMap<>();
    
    @Override
    public Map<UUID, String> getAttachments() {
        return this.getPermitAttachments();
    }
}
