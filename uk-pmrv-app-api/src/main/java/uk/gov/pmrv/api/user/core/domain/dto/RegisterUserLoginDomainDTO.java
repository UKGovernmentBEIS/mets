package uk.gov.pmrv.api.user.core.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserLoginDomainDTO {

    @NotNull
    private AccountType loginDomain;
}
