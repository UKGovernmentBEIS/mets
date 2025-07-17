package uk.gov.pmrv.api.user.verifier.service;

import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.AuthService;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.transform.VerifierUserMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerifierUserAuthService {

    private final AuthService authService;
    private final UserAuthService userAuthService;
    private final VerifierUserMapper verifierUserMapper;

    public VerifierUserDTO getVerifierUserById(String userId) {
        return verifierUserMapper.toVerifierUserDTO(authService.getUserRepresentationById(userId));
    }
    
    public Optional<UserInfoDTO> getUserByEmail(String email) {
    	return userAuthService.getUserByEmail(email);
    }

    public void updateVerifierUser(VerifierUserDTO verifierUserDTO) {
        UserRepresentation updatedUser = verifierUserMapper.toUserRepresentation(verifierUserDTO);
        authService.saveUser(updatedUser);
    }

    /**
     * @param verifierUserInvitation the {@link VerifierUserInvitationDTO} containing invited user's info.
     * @return the unique id for the user
     */
    @Transactional
    public String registerInvitedVerifierUser(VerifierUserInvitationDTO verifierUserInvitation) {
        UserRepresentation newUserRepresentation = verifierUserMapper.toUserRepresentation(verifierUserInvitation);
        return authService.saveUser(newUserRepresentation);
    }
}
