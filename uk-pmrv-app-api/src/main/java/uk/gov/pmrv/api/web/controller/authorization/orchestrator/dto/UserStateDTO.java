package uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStateDTO {

    private String userId;

    private RoleType roleType;

    private AccountType lastLoginDomain;

    @JsonUnwrapped
    private UserDomainsLoginStatusInfo domainsLoginStatuses;
}
