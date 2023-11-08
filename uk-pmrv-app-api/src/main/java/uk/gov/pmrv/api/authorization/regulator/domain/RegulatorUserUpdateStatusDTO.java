package uk.gov.pmrv.api.authorization.regulator.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegulatorUserUpdateStatusDTO {

    @NotBlank
	private String userId;

    @NotNull
    private AuthorityStatus authorityStatus;
    
}
