package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitTransferBApplicationSubmittedRequestActionPayload extends RequestActionPayload {

    @NotNull
    @Valid
    private PermitTransferDetails permitTransferDetails;

    @NotNull
    @Valid
    private PermitTransferDetailsConfirmation permitTransferDetailsConfirmation;

    @NotNull
    @Valid
    private PermitType permitType;

    @NotNull
    @Valid
    private Permit permit;

    @Valid
    @NotNull
    private InstallationOperatorDetails installationOperatorDetails;

    @Builder.Default
    private Map<String, List<Boolean>> permitSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> permitAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getPermitAttachments();
    }
}
