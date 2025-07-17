package uk.gov.pmrv.api.workflow.request.flow.common.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

@Service
@RequiredArgsConstructor
public class RequestAccountContactQueryService {

    private final AccountContactQueryService accountContactQueryService;
    private final UserAuthService userAuthService;
    
    public Optional<UserInfoDTO> getRequestAccountContact(Request request, AccountContactType contactType) {
        return accountContactQueryService
            .findContactByAccountAndContactType(request.getAccountId(), contactType)
            .map(userAuthService::getUserByUserId);
    }

    public Optional<UserInfoDTO> getRequestAccountPrimaryContact(Request request) {
        return getRequestAccountContact(request, AccountContactType.PRIMARY);
    }

    public Optional<UserInfoDTO> getRequestAccountServiceContact(Request request) {
        return getRequestAccountContact(request, AccountContactType.SERVICE);
    }

}
