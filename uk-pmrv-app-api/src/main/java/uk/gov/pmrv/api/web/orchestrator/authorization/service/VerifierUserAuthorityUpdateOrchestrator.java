package uk.gov.pmrv.api.web.orchestrator.authorization.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.verifier.domain.VerifierAuthorityUpdateDTO;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityUpdateService;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserNotificationGateway;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VerifierUserAuthorityUpdateOrchestrator {

    private final VerifierAuthorityUpdateService verifierAuthorityUpdateService;
    private final VerifierUserNotificationGateway verifierUserNotificationGateway;

    @Transactional
    public void updateVerifierAuthorities(List<VerifierAuthorityUpdateDTO> verifiersUpdate, Long verificationBodyId) {
        List<String> activatedVerifiers = verifierAuthorityUpdateService
                .updateVerifierAuthorities(verifiersUpdate, verificationBodyId);

        if(!activatedVerifiers.isEmpty()){
            verifierUserNotificationGateway.notifyUsersUpdateStatus(activatedVerifiers);
        }
    }
}
