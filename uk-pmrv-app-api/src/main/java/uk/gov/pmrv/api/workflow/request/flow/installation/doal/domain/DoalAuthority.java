package uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoalAuthority {

    @Valid
    @NotNull
    private DateSubmittedToAuthority dateSubmittedToAuthority;

    @Valid
    @NotNull
    private DoalAuthorityResponse authorityResponse;
}
