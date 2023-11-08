package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermitTransferDetails {

    @NotNull
    @Size(max = 10000)
    private String reason;

    @Builder.Default
    private Set<UUID> reasonAttachments = new HashSet<>();

    @NotNull
    private LocalDate transferDate;

    @NotNull
    private TransferParty payer;

    @NotNull
    private TransferParty aerLiable;

    @NotNull
    @Size(min = 9, max = 9)
    private String transferCode;
}
