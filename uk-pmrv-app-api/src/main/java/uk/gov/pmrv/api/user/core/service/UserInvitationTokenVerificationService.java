package uk.gov.pmrv.api.user.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.authorization.core.service.AuthorityService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.token.JwtTokenAction;
import uk.gov.netz.api.token.JwtTokenService;

@Service
@RequiredArgsConstructor
public class UserInvitationTokenVerificationService {

    private final JwtTokenService jwtTokenService;
    private final AuthorityService<?> authorityService;

    public AuthorityInfoDTO verifyInvitationTokenForPendingAuthority(String invitationToken, JwtTokenAction tokenAction) {
        String authorityUuid = jwtTokenService.resolveTokenActionClaim(invitationToken, tokenAction);
        return authorityService.findAuthorityByUuidAndStatusPending(authorityUuid)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));
    }

}
