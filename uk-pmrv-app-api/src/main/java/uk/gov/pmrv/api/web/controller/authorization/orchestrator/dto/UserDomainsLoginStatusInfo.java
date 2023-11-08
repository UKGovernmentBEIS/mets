package uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

import java.util.EnumMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDomainsLoginStatusInfo {

    private EnumMap<AccountType, LoginStatus> domainsLoginStatuses;
}
