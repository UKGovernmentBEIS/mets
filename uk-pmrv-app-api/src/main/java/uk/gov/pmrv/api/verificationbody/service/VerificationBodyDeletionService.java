package uk.gov.pmrv.api.verificationbody.service;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityDeletionService;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.verificationbody.domain.event.VerificationBodyDeletedEvent;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyRepository;

@Service
@RequiredArgsConstructor
public class VerificationBodyDeletionService {

    private final VerificationBodyRepository verificationBodyRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final VerifierAuthorityDeletionService verifierAuthorityDeletionService;

    @Transactional
    public void deleteVerificationBodyById(Long verificationBodyId) {
        if(!verificationBodyRepository.existsById(verificationBodyId)){
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, verificationBodyId);
        }

        // Delete VB
        verificationBodyRepository.deleteById(verificationBodyId);

        // VerificationBodyDeletedEvent could be used for deleting authorities
        // but direct service call was preferred to avoid introducing dependency from authorization to verification body domain (for the VerificationBodyDeletedEvent).
        // On the other hand, event was preferred for notifying the account domain in order to avoid introducing dependency from verification body to account domain.
        verifierAuthorityDeletionService.deleteVerifierAuthorities(verificationBodyId);
        eventPublisher.publishEvent(new VerificationBodyDeletedEvent(verificationBodyId));
    }
}
