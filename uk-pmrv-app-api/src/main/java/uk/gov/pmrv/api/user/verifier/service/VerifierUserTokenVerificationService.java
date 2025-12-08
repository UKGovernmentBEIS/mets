package uk.gov.pmrv.api.user.verifier.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.token.JwtTokenAction;
import uk.gov.pmrv.api.user.core.service.UserInvitationTokenVerificationService;

@Service
@RequiredArgsConstructor
public class VerifierUserTokenVerificationService {

    private final UserInvitationTokenVerificationService userInvitationTokenVerificationService;

    public AuthorityInfoDTO verifyInvitationToken(String invitationToken, AppUser currentUser) {
        return userInvitationTokenVerificationService
            .verifyInvitationToken(invitationToken, JwtTokenAction.VERIFIER_INVITATION, currentUser);
    }
}
