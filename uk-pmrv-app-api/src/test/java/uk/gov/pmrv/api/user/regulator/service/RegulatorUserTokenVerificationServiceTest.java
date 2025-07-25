package uk.gov.pmrv.api.user.regulator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.token.JwtTokenAction;
import uk.gov.pmrv.api.user.core.service.UserInvitationTokenVerificationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegulatorUserTokenVerificationServiceTest {

    @InjectMocks
    private RegulatorUserTokenVerificationService regulatorUserTokenVerificationService;

    @Mock
    private UserInvitationTokenVerificationService userInvitationTokenVerificationService;

    @Test
    void verifyInvitationToken() {
        String invitationToken = "invitationToken";
        JwtTokenAction tokenAction = JwtTokenAction.REGULATOR_INVITATION;
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .id(1L)
            .userId("user")
            .authorityStatus(AuthorityStatus.PENDING)
            .accountId(1L)
            .build();

        when(userInvitationTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken, tokenAction))
            .thenReturn(authorityInfo);

        AuthorityInfoDTO actual = regulatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken);

        assertEquals(authorityInfo, actual);

    }

}