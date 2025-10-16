package uk.gov.pmrv.api.workflow.request.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.user.core.service.UserLoginDomainService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestTypeOperatorMapperService implements RequestTypeToRoleMapperService {

   private final UserLoginDomainService userLoginDomainService;

   public Set<RequestType> getRequestTypes(AppUser appUser) {

        AccountType accountType = userLoginDomainService.getUserLastLoginDomain(appUser.getUserId());

        return Arrays.stream(RequestType.values())
                .filter(type-> (accountType.equals(type.getAccountType()) || type.getAccountType()==null)
                        && type.getRoleTypes().contains(RoleTypeConstants.OPERATOR))
                .collect(Collectors.toSet());
    }

    public String getRoleType() {
        return RoleTypeConstants.OPERATOR;
    }
}
