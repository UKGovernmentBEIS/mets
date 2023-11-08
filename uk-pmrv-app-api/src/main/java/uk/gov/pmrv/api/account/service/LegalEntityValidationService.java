package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.repository.LegalEntityRepository;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class LegalEntityValidationService {

    private final LegalEntityRepository legalEntityRepository;

    public void validateNameExistenceInOtherActiveLegalEntities(String name, Long legalEntityId) {
        if(legalEntityRepository.existsByNameAndStatusAndIdNot(name, LegalEntityStatus.ACTIVE, legalEntityId)) {
            throw new BusinessException(ErrorCode.LEGAL_ENTITY_ALREADY_EXISTS);
        }
    }

    public boolean isExistingActiveLegalEntityName(String leName) {
        return isExistingActiveLegalEntityName(leName, null);
    }

    /**
     * Checks if an active legal entity exists for the provided user.
     *
     * @param leName Legal entity name
     * @param pmrvUser {@link PmrvUser}
     * @return True if an active legal entity exists
     */
    public boolean isExistingActiveLegalEntityName(String leName, PmrvUser pmrvUser) {
        if(pmrvUser == null) {
            return legalEntityRepository.existsByNameAndStatus(leName, LegalEntityStatus.ACTIVE);
        }

        switch (pmrvUser.getRoleType()) {
            case REGULATOR:
                return legalEntityRepository.existsByNameAndStatus(leName, LegalEntityStatus.ACTIVE);
            case OPERATOR:
                return legalEntityRepository.existsActiveLegalEntityNameInAnyOfAccounts(leName, pmrvUser.getAccounts());
            default:
                throw new BusinessException(ErrorCode.LEGAL_ENTITY_NOT_ASSOCIATED_WITH_USER);
        }
    }
}
